package it.unitn.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Stopwatch;
import it.unitn.control.DetectorListener;
import it.unitn.control.DetectorTextBase;
import it.unitn.dto.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetectorGUI implements ActionListener, DetectorListener {
    private static final String IMG_PATH = "./src/main/resources/logo.png";

    private DetectorTextBase detectorTextBase = new DetectorTextBase();

    private JFrame frame;
//    private DetectTask detectTask;

    //GUI Components
    private ClosableTabbedPane mainTab;
    private JButton monitorLogBrowseButton;
    private JTextField monitoringFileNamePatternField;
    private JButton listenerLogBrowseButton;
    private JButton addNewListenerButton;
    private JTextField monitorSearchDirField;
    private JTextField listenerSearchDirField;

    private List<ListenerSettingPanel> listenerSettingPanelList = new ArrayList<>();

    private JButton startButton;
    private JButton stopButton;
    private JList resultsList;
    private StatusBar statusBar;
    private JCheckBox searchGreedily;
    private JCheckBox recurseMonitoringSubDirs;

    /**
     * Singleton
     */

    private static DetectorGUI detectorGUI = new DetectorGUI();

    public static DetectorGUI getInstance() {
        return detectorGUI;
    }

    private DetectorGUI() {
        //register call back in detector
        detectorTextBase.registerDetectorListener(this);

        frame = new JFrame("Cryptojackingtrap :: Detector");
        try {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int centerX = screenSize.width / 2;
            int centerY = screenSize.height / 2;
            Integer width = 400;
            Integer height = 600;
            frame.setBounds(centerX - width / 2, height / 10, 400, 900);
        } catch (Exception ex) {
            frame.setSize(400, 700);
        }

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        createWidgets();
        //todo need?
        ToolTipManager ttm = ToolTipManager.sharedInstance();
        ttm.setInitialDelay(100);
        ttm.setDismissDelay(30000);
    }

    private void createWidgets() {
        Container c = frame.getContentPane();
        c.setLayout(new BorderLayout());


        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        // topPanel.setLayout();

        /**
         *logo
         */

        JPanel starterPanel = new JPanel();
        starterPanel.setLayout(new FlowLayout());
        starterPanel.setPreferredSize(new Dimension(185, 85));

        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(IMG_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageIcon logoImage = new ImageIcon(img);
        JLabel logoLable = new JLabel(logoImage);
        starterPanel.add(logoLable);

        /**
         * Monitor settings
         */
        JPanel monitoringControlPanel = new JPanel();
        monitoringControlPanel.setBorder(BorderFactory.createTitledBorder(" Monitoring Settings "));
        monitoringControlPanel.setLayout(new FlowLayout());
        monitoringControlPanel.setPreferredSize(new Dimension(370, 140));

        JLabel monitorDirLabel = new JLabel("File/Dir Path *");
        monitoringControlPanel.add(monitorDirLabel);

        monitorSearchDirField = new JTextField(20);
        monitorSearchDirField.addActionListener(this);
        monitoringControlPanel.add(monitorSearchDirField);

        monitorLogBrowseButton = new JButton("...");
        monitorLogBrowseButton.addActionListener(this);
        monitoringControlPanel.add(monitorLogBrowseButton);

        JLabel monitoringFileNamePattern = new JLabel("File name pattern");
        monitoringControlPanel.add(monitoringFileNamePattern);

        monitoringFileNamePatternField = new JTextField(23);
        monitoringFileNamePatternField.addActionListener(this);
        monitoringControlPanel.add(monitoringFileNamePatternField, BorderLayout.LINE_START);

        recurseMonitoringSubDirs = new JCheckBox("Recurse sub-directories");
        recurseMonitoringSubDirs.setToolTipText("If this box is ticked the search will scan through any sub-directories found.");
        recurseMonitoringSubDirs.setSelected(true);
        monitoringControlPanel.add(recurseMonitoringSubDirs, BorderLayout.WEST);


        /**
         * Listener settings
         */
        JPanel listenersControlPanel = new JPanel();
        listenersControlPanel.setBorder(BorderFactory.createTitledBorder(" Listener(s) Settings "));
        listenersControlPanel.setLayout(new FlowLayout());
        listenersControlPanel.setPreferredSize(new Dimension(370, 250));

        mainTab = new ClosableTabbedPane();
        mainTab.setPreferredSize(new Dimension(360, 190));
        listenersControlPanel.add(mainTab, BorderLayout.WEST);

        prepareNewListenerTab();//first tab

        addNewListenerButton = new JButton(" + ");
        addNewListenerButton.setToolTipText("Add a new listener setting");


        addNewListenerButton.addActionListener(evt -> newItemActionPerformed(evt));
        listenersControlPanel.add(addNewListenerButton, BorderLayout.LINE_END);


        /**
         * General elements
         */
        JPanel generalControlPanel = new JPanel();
        //generalControlPanel.setBorder(BorderFactory.createTitledBorder("General Settings"));
        generalControlPanel.setLayout(new FlowLayout());
        generalControlPanel.setPreferredSize(new Dimension(370, 240));

        searchGreedily = new JCheckBox("Continue searching after detection");
        searchGreedily.setToolTipText("If this box is ticked the search will be continued for increasing the preciousness.");
        searchGreedily.setSelected(true);
        generalControlPanel.add(searchGreedily);


        startButton = new JButton("Start");
        startButton.addActionListener(this);
        generalControlPanel.add(startButton);

        stopButton = new JButton("Stop");
        stopButton.addActionListener(this);
        stopButton.setEnabled(false);
        generalControlPanel.add(stopButton);

        /**
         * Finalizing
         */
        topPanel.add(starterPanel);
        topPanel.add(monitoringControlPanel);
        topPanel.add(listenersControlPanel);
        topPanel.add(generalControlPanel);

        c.add(topPanel);

//        resultsList = new JList(new DefaultListModel());
//        resultsList.setCellRenderer(new SearchResponseListCellRenderer(searchTermField, searchTermIsRegex, showResultList));
//        resultsList.addMouseListener(new MouseAdapter() {
//            public void mouseClicked(MouseEvent evt) {
//                if (evt.getClickCount() > 1) {
//                    int index = resultsList.locationToIndex(evt.getPoint());
//                    SearchResponse result = (SearchResponse) resultsList.getModel().getElementAt(index);
//                    openExplorer(result);
//                }
//            }
//        });
//        JScrollPane scrollPane = new JScrollPane(resultsList);
//        c.add(scrollPane, BorderLayout.CENTER);

        statusBar = new StatusBar();
        statusBar.appendText("Ready");
        c.add(statusBar, BorderLayout.SOUTH);
    }

    private void prepareNewListenerTab() {
        JPanel newListenerControlPanel = new JPanel();
        newListenerControlPanel.setLayout(new FlowLayout());
        newListenerControlPanel.setPreferredSize(new Dimension(365, 200));

        mainTab.addTab("Listener", null, newListenerControlPanel, "New cryptocurrency listener settings");

        JLabel cryptoNameLabel = new JLabel("Cryptocurrency Name *");
        newListenerControlPanel.add(cryptoNameLabel);

        JTextField cryptoNameField = new JTextField(20);
        cryptoNameField.addActionListener(this);
        newListenerControlPanel.add(cryptoNameField);

        JPanel browserPanel = new JPanel();
        browserPanel.setLayout(new FlowLayout());
        browserPanel.setPreferredSize(new Dimension(365, 30));
        JLabel listenerDirLabel = new JLabel("File/Dir Path *");
        browserPanel.add(listenerDirLabel);

        listenerSearchDirField = new JTextField(20);
        listenerSearchDirField.addActionListener(this);
        browserPanel.add(listenerSearchDirField);

        listenerLogBrowseButton = new JButton("...");
        listenerLogBrowseButton.addActionListener(this);
        browserPanel.add(listenerLogBrowseButton);
        newListenerControlPanel.add(browserPanel);

        JLabel listenerFileNamePattern = new JLabel("File name pattern");
        newListenerControlPanel.add(listenerFileNamePattern);

        JTextField listenerFileNamePatternField = new JTextField(23);
        listenerFileNamePatternField.addActionListener(this);
        newListenerControlPanel.add(listenerFileNamePatternField, BorderLayout.LINE_START);

        JCheckBox recurseListenerSubDirs = new JCheckBox("Recurse sub-directories");
        recurseListenerSubDirs.setToolTipText("If this box is ticked the search will scan through any sub-directories found.");
        recurseListenerSubDirs.setSelected(true);
        newListenerControlPanel.add(recurseListenerSubDirs);

        /**
         * making input parts of each listener accessible
         */
        ListenerSettingPanel lisPanel = new ListenerSettingPanel();
        lisPanel.setCryptoNameField(cryptoNameField);
        lisPanel.setListenerSearchDirField(listenerSearchDirField);
        lisPanel.setListenerFileNamePatternField(listenerFileNamePatternField);
        lisPanel.setRecurseListenerSubDirs(recurseListenerSubDirs);
        listenerSettingPanelList.add(lisPanel);
    }

    private void newItemActionPerformed(java.awt.event.ActionEvent evt) {
        prepareNewListenerTab();
    }

    public void show() {
        frame.setVisible(true);
    }

//    public void setSearchDir(File searchDir) {
//        searchDirField.setText(searchDir.getAbsolutePath());
//    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == startButton || evt.getSource() instanceof JTextField) {
            try {
                DetectorSetting detectorSetting = getFormData();
                startSearch(detectorSetting);
            } catch (Exception ex) {
                StringBuilder msg = new StringBuilder(ex.getMessage());
                Throwable t = ex.getCause();
                if (t != null) msg.append("\n" + t.getMessage());

                JOptionPane.showMessageDialog(frame, msg, "Ooops!", JOptionPane.ERROR_MESSAGE);
            }
        } else if (evt.getSource() == stopButton) {
            try {
                //stopSearch();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex, "Ooops!", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } else if (evt.getSource() == monitorLogBrowseButton) {
            browse(monitorSearchDirField);
        } else if (evt.getSource() == listenerLogBrowseButton) {
            browse(listenerSearchDirField);
        }
    }

    private DetectorSetting getFormData() {
        String monitoringPath = monitorSearchDirField.getText();
        String monitoringPattern = monitoringFileNamePatternField.getText();
        Boolean isRecursive = recurseMonitoringSubDirs.isSelected();
        FileSetting monitoringFileSetting = new FileSetting(monitoringPath, monitoringPattern, isRecursive);
        MonitorSetting monitorSetting = new MonitorSetting(monitoringFileSetting);

        List<ListenerSetting> listenerSettingList = new ArrayList<>();
        for (ListenerSettingPanel panel : listenerSettingPanelList) {
            FileSetting fileSetting = new FileSetting(
                    panel.getListenerSearchDirField().getText(),
                    panel.getListenerFileNamePatternField().getText(),
                    panel.getRecurseListenerSubDirs().isSelected());
            ListenerSetting listenerSetting = new ListenerSetting(panel.getCryptoNameField().getText(), fileSetting);
            listenerSettingList.add(listenerSetting);
        }
        ListenerSettings listenerSettings = new ListenerSettings(listenerSettingList);
        DetectorSetting detectorSetting = new DetectorSetting(listenerSettings, monitorSetting, searchGreedily.isSelected());
        return detectorSetting;
    }

    Stopwatch timer;

    private void startSearch(DetectorSetting detectorSetting) throws Exception {
        detectorTextBase.setDetectorSetting(detectorSetting);
        timer = Stopwatch.createStarted();
        new Thread(detectorTextBase).start();
    }

    @Override
    public void detectionResultCallback(DetectorResult detectorResult) {
        timer.stop();
        if (detectorResult != null) {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String result = "Cryptojacking result is " + (detectorResult.getDetectorAlert() ?
                    "positive" : "negative") + ", in " + timer;
            String json = null;
            try {
                json = ow.writeValueAsString(detectorResult);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            appendStatus("-----------------------");
            appendStatus("-----------------------");
            appendStatus(result + " - " + "details: " + json);
            JOptionPane.showMessageDialog(null, result);
        }
    }

    public void appendStatus(String message) {
        statusBar.appendText("\n" + message);
    }

    /*private void stopSearch() throws Exception {
        if (detectTask == null || detectTask.isDone()) {
            throw new Exception("Search is not running.");
        }
        detectTask.cancel(true);
    }

    private void startSearch() throws Exception {
        if (detectTask != null && !detectTask.isDone()) {
            throw new Exception("Search already running please stop first.");
        } else {
            detectTask = new SearchTask(startButton, stopButton, statusBar);

            detectTask.setIgnoreSVNEntries(searchGreedily.isSelected());
            detectTask.setRecurseSubdirectories(recurseSubdirs.isSelected());
            detectTask.setRegexSearchTerm(searchTermIsRegex.isSelected());
            detectTask.setShowResultList(showResultList.isSelected());

            File dir = new File(searchDirField.getText());
            if (!dir.exists()) {
                throw new Exception("Can't find directory.");
            }
            if (!dir.isDirectory()) {
                dir = dir.getParentFile();
            }
            detectTask.setDir(dir);

            String fileNameText = cryptoNameField.getText();
            if (fileNameText != null & !fileNameText.isEmpty()) {
                try {
                    Pattern fileNameTerm = Pattern.compile(fileNameText);
                    detectTask.setFileName(fileNameTerm);
                } catch (PatternSyntaxException pse) {
                    throw new Exception("Invalid file name term, use a regex.", pse);
                }
            }

            String searchTermText = searchTermField.getText();
            if (searchTermIsRegex.isSelected()) {
                try {
                    detectTask.setSearchTermPattern(searchTermText);
                } catch (PatternSyntaxException pse) {
                    throw new Exception("Invalid search term, not a valid regex.", pse);
                }
            } else {
                detectTask.setSearchTerm(searchTermText);
            }

            detectTask.setListener(this);
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            ((DefaultListModel) (resultsList.getModel())).removeAllElements();
            detectTask.execute();
        }
    }

    private void openExplorer(SearchResponse result) {
        try {
            Runtime.getRuntime().exec(new String[]{"explorer.exe", result.getFile().getAbsolutePath()});
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(frame, ioe, "Ooops!", JOptionPane.ERROR_MESSAGE);
        }
    }
*/
    private void browse(JTextField jTextField) {
        File dir = new File(jTextField.getText());
        if (!dir.exists()) {
            dir = new File("c:\\");
        } else if (!dir.isDirectory()) {
            dir = dir.getParentFile();
        }

        JFileChooser fileChooser = new JFileChooser(dir);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(false);
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            jTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }
}
