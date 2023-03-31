package it.unitn.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Setter
@Getter
@ToString
public class FileSetting {
    /**
     * path can be a single file or a directory
     */
    private String path;
    private Pattern fileNamePattern;
    private Boolean isRecursive;

    public FileSetting(String path, String fileNamePattern, Boolean isRecursive) {
        this.path = path;
        if (fileNamePattern != null) {
            Pattern monitoringPattern = Pattern.compile(fileNamePattern);
            this.fileNamePattern = monitoringPattern;
        }
        this.isRecursive = isRecursive;
    }

    /**
     * consider the file patterns and recursion settings and return the final targeted files.
     *
     * @return
     */
    public List<File> getFiles() {
        return getFiles(new File(path));
    }

    /**
     * @param inputFile including file and directory
     * @return
     */
    private List<File> getFiles(File inputFile) {
        List<File> result = new ArrayList<>();
        if (inputFile.isFile()) {
            if (fileNamePattern == null || fileNamePattern.equals("") ||
                    fileNamePattern.matcher(inputFile.getName()).find()) {
                result.add(inputFile);
            }
        } else {
            for (File file : inputFile.listFiles()) {
                if (file.isDirectory() && isRecursive) {
                    List<File> addedFiles = getFiles(file);
                    result.addAll(addedFiles);
                } else if (fileNamePattern == null || fileNamePattern.equals("") ||
                        fileNamePattern.matcher(file.getName()).find()) {
                    result.add(file);
                }
            }
        }
        return result;
    }
}
