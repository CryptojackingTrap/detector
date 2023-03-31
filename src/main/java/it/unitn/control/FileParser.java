package it.unitn.control;

import it.unitn.deprecated.dto.xmf.MemoryReadLog;
import it.unitn.deprecated.dto.xmf.MemoryReadLogs;
import it.unitn.dto.DetectorSetting;
import it.unitn.dto.ListenerSetting;
import it.unitn.dto.ListenerSettings;
import it.unitn.dto.MonitorSetting;
import it.unitn.dto.imf.Block;
import it.unitn.dto.xmf.BlockLog;
import it.unitn.dto.xmf.BlockLogs;
import it.unitn.view.CentralConsole;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Todo review and declare deprecated parts of this file
 */
public class FileParser {
    private static final String DATE_FORMAT_PATTERN = "(yyyy/MM/dd, HH:mm:ss)";
    private static short DEBUG_HEADER_COUNT = 3;
    private static long BLOCK_PROPAGATION_MIN_TIME = 1000l; // cover time in
    // millisecond.
    private static long BLOCK_PROPAGATION_MAX_TIME = 30000l; // cover time in
    // millisecond.
    // block line size is not constant because of supporting numerous cryptocurrencies
    private static int LISTENER_CHUNK_SIZE = 1000;

    // debug line size is not constant
    private static int MONITOR_CHUNK_SIZE = 1000;

    /**
     * consider all supported cryptocurrencies and for each one, considering the monitoring files covered time, split
     * find the corresponding hashes for the listener files and return a list of BlockReadLogSetting that in
     * each, the cryptoName, hash value of that crypto and subset of a monitoring data for that period of time are
     * collected in BlockReadLogSetting data structure.
     * <p>
     * todo Major review and convert the file line usage to MemoryReadLog here and detectorTestBase
     *
     * @param detectorSetting
     * @return
     */
    public List<BlockReadLog> getBlockReadLogs(DetectorSetting detectorSetting) throws IOException, ParseException {
        List<BlockReadLog> blockReadLogs = new ArrayList<>();
        List<File> monitorFiles = detectorSetting.getMonitorSetting().getMonitoringFileSetting().getFiles();
        if (monitorFiles.size() > 1) {
            throw new RuntimeException("extend the detector implementation to support multiple monitorFiles");
        }
        //Listener:
        BlockLogs blockLogs = this.getBlockLogs(detectorSetting.getListenerSettings());
        for (BlockLog blockLog : blockLogs.getBlockLogList()) {
            for (Block block : blockLog.getBlockList()) {
                BlockReadLog blockReadLog = new BlockReadLog();

                //1) MemoryLog:
                blockReadLog.setMonitorFile(monitorFiles.get(0));
                //TODO split the files based on time, now I assume we have one monitor file and all is relevant
                // to all hash values

                //2) ReadLog:
                blockReadLog.setCryptocurrencyName(blockLog.getCryptocurrencyName());
                blockReadLog.setHashValue(block.getHeaderHash());
                blockReadLogs.add(blockReadLog);
            }
        }
        return blockReadLogs;
    }

    public MemoryReadLogs getMemoryReadLogs(MonitorSetting monitorSetting) throws IOException,
            ParseException {
        List<MemoryReadLog> memoryReadLogList = new ArrayList<MemoryReadLog>();
        RandomAccessFile debugFile = new RandomAccessFile(monitorSetting.getMonitoringFileSetting().getPath(), "r");
        FileChannel inChannel = debugFile.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(MONITOR_CHUNK_SIZE);
        String lastLineRemaning = "";
        while (inChannel.read(buffer) > 0) {
            buffer.flip();
            String chunk = new String(buffer.array());
            chunk = lastLineRemaning + chunk;
            String[] lines = chunk.split("\n");
            for (int i = DEBUG_HEADER_COUNT; i < lines.length - 1; i++) {
                String line = lines[i];
                MemoryReadLog memoryReadLog = getMemoryReadLog(line);
                memoryReadLogList.add(memoryReadLog);
            }
            lastLineRemaning = lines[lines.length - 1];
            buffer.clear();
        }
        inChannel.close();
        debugFile.close();
        MemoryReadLogs memoryReadLogs = new MemoryReadLogs(memoryReadLogList);
        return memoryReadLogs;
    }

    private MemoryReadLog getMemoryReadLog(String line) throws ParseException {
        try {
            //split the line by one or more space(s) greedily
            String[] parts = line.split("\\s++");
            //delete null elements of parts
            parts = Arrays.stream(parts)
                    .filter(s -> (s != null && s.length() > 0))
                    .toArray(String[]::new);
            String readCount = null;
            String readContent;
            if (parts.length == 2) {
                readCount = parts[0];
                readContent = parts[1];
            } else {
                //skip processing the line
                readContent = line;
            }
            MemoryReadLog memoryReadLog = new MemoryReadLog(readCount, readContent);
            return memoryReadLog;
        } catch (Throwable t) {
            CentralConsole.log("error in processing memory read line:" + line);
            return null;
        }

    }

    private BlockLogs getBlockLogs(ListenerSetting listenerSetting) throws IOException, ParseException {
        BlockLogs blockLogs = new BlockLogs();
        List<Block> blockList = getBlockList(listenerSetting.getListenerLogsFileSetting().getPath());
        BlockLog blockLog = new BlockLog(listenerSetting.getCryptocurrencyName(), blockList);
        blockLogs.add(blockLog);
        return blockLogs;
    }

    public BlockLogs getBlockLogs(ListenerSettings listenerSettings) throws IOException, ParseException {
        BlockLogs blockLogs = new BlockLogs();
        for (ListenerSetting listenerSetting : listenerSettings.getListenerSettingList()) {
            blockLogs.addAll(this.getBlockLogs(listenerSetting));
        }
        return blockLogs;
    }

    private List<Block> getBlockList(String listenerLogsFilePath) throws IOException,
            ParseException {
        List<Block> blocks = new ArrayList<Block>();
        RandomAccessFile blockFile = new RandomAccessFile(listenerLogsFilePath, "r");
        FileChannel inChannel = blockFile.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(LISTENER_CHUNK_SIZE);
        String remainingFromPreviousChunk = "";
        while (inChannel.read(buffer) > 0) {
            buffer.flip();
            String chunk = new String(buffer.array());
            chunk = remainingFromPreviousChunk + chunk;
            String[] lines = chunk.split("\n");

            /**
             * process every line except last line in chunk (it may be
             * incomplete) and process it with the next chunk.
             */
            remainingFromPreviousChunk = lines[lines.length - 1];
            for (int i = 0; i < lines.length - 1; i++) {
                String line = lines[i];
                Block block = getBlock(line);
                blocks.add(block);
            }
            buffer.clear();
            /*
             * clear don't clean the content of the buffer.
             */
            buffer = ByteBuffer.allocate(LISTENER_CHUNK_SIZE);
//			buffer.put(emptyBuffer);

        }

        /**
         * process last line if it is remained
         */
        if (!remainingFromPreviousChunk.equals("")) {
            Block block = getBlock(remainingFromPreviousChunk);
            blocks.add(block);
        }

        inChannel.close();
        blockFile.close();
        return blocks;
    }

    /**
     * pattern of each line:
     * 000000000000000007B858CEED9681A0487D15CC60E90A7D2AEC96BED94D5795
     * (2016/01/23, 13:11:00) (2016/01/23, 13:09:41)
     *
     * @param line
     * @return
     * @throws ParseException
     */
    private Block getBlock(String line) throws ParseException {
        try {
            Integer i = line.indexOf(" (");
            Integer i2 = line.lastIndexOf(" (");

            String hash = line.substring(0, i);
            String receiveDateStr = line.substring(i + 1, i2);
            String blockDateStr = line.substring(i2 + 1);
            DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
            Date receiveDate = dateFormat.parse(receiveDateStr);
            Date blockDate = dateFormat.parse(blockDateStr);
            Block block = new Block(hash, receiveDate, blockDate);
            return block;
        } catch (Throwable e) {
            CentralConsole.log("error in processing block line:" + line);
            return null;
        }
    }
}
