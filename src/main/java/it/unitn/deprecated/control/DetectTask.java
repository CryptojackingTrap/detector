//package it.unitn.draft;
//
//import it.unitn.textFileSearch.SearchHit;
//import it.unitn.textFileSearch.SearchListener;
//import it.unitn.textFileSearch.SearchResponse;
//import it.unitn.textFileSearch.StatusBar;
//
//import javax.swing.*;
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//import java.text.DecimalFormat;
//import java.util.List;
//import java.util.regex.Pattern;
//import java.util.regex.PatternSyntaxException;
//
//public class DetectTask extends SwingWorker<Object,String> {
//
//  private File dir;
//  private Pattern fileName;
//  private String searchTerm;
//  private Pattern searchTermPattern;
//  private boolean recurseSubdirectories = true;
//  private boolean ignoreSVNEntries = true;
//  private boolean regexSearchTerm = false;
//  private boolean showResultList = false;
//
//  private SearchListener listener;
//
//  private JButton startButton;
//  private JButton stopButton;
//  private StatusBar statusBar;
//
//  private int searched;
//  private int scanned;
//  private int found;
//  private long time;
//
//  public DetectTask(JButton startButton, JButton stopButton, StatusBar statusBar) {
//    super();
//    this.startButton = startButton;
//    this.stopButton = stopButton;
//    this.statusBar = statusBar;
//  }
//
//  @Override
//  protected Object doInBackground() throws Exception {
//    time = System.currentTimeMillis();
//    doSearch(dir);
//    return null;
//  }
//
//  @Override
//  protected void done() {
//    startButton.setEnabled(true);
//    stopButton.setEnabled(false);
//
//    time = System.currentTimeMillis() - time;
//
//    DecimalFormat format = new DecimalFormat("0.000");
//    double seconds = ((double)time)/1000;
//    publish("Finished, searched " + searched + ", scanned " + scanned + " and found " + found + " in " + format.format(seconds) + " seconds.");
//  }
//
//  @Override
//  protected void process(List<String> msgs) {
//    statusBar.setText(msgs.get(msgs.size()-1));
//  }
//
//  public void setDir(File dir) {
//    if (dir == null || !dir.exists() || !dir.isDirectory()) {
//      throw new IllegalArgumentException("Not a directory.");
//    }
//    this.dir = dir;
//  }
//
//  public void setFileName(Pattern fileName) {
//    this.fileName = fileName;
//  }
//
//  public void setSearchTerm(String searchTerm) {
//    this.searchTerm = searchTerm;
//  }
//
//  public void setRegexSearchTerm(boolean regexSearchTerm) {
//    this.regexSearchTerm = regexSearchTerm;
//  }
//
//  public void setSearchTermPattern(String searchTerm) throws PatternSyntaxException {
//    this.searchTermPattern = Pattern.compile(searchTerm);
//  }
//
//  public void setListener(SearchListener listener) {
//    this.listener = listener;
//  }
//
//  public void setIgnoreSVNEntries(boolean ignoreSVNEntries) {
//    this.ignoreSVNEntries = ignoreSVNEntries;
//  }
//
//  public void setRecurseSubdirectories(boolean recurseSubdirectories) {
//    this.recurseSubdirectories = recurseSubdirectories;
//  }
//
//  private void doSearch(File directory) {
//    for (File file : directory.listFiles()) {
//      if (file.isDirectory() && recurseSubdirectories) {
//        if (ignoreSVNEntries && file.getName().endsWith(SVN_DIR_SUFFIX)) {
//          continue;
//        }
//        publish("Searched " + searched + ", scanned " + scanned + " - " + file.getAbsolutePath());
//        doSearch(file);
//      } else if (fileName == null || fileName.matcher(file.getName()).find()) {
//
//        if (ignoreSVNEntries && file.getName().endsWith(SVN_BASE_SUFFIX)) {
//          continue;
//        }
//
//        publish("Searched " + searched + ", scanned " + scanned + " - " + file.getAbsolutePath());
//        if (scanRequired()) {
//          scanFile(file);
//          scanned++;
//        } else {
//          SearchResponse response = new SearchResponse();
//          response.setFile(file);
//          listener.handleSearchResponse(response);
//          found++;
//        }
//      }
//      searched++;
//    }
//  }
//
//  private boolean scanRequired() {
//    if (regexSearchTerm) {
//      return searchTermPattern != null;
//    } else {
//      return searchTerm != null && !searchTerm.isEmpty();
//    }
//  }
//
//  private void scanFile(File file) {
//    SearchResponse response = new SearchResponse();
//    response.setFile(file);
//    BufferedReader reader = null;
//    try {
//      reader = new BufferedReader(new FileReader(file));
//      String line = null;
//      int lineNumber = 0;
//      while ((line = reader.readLine()) != null) {
//        lineNumber++;
//        if (lineContainsTerm(line)) {
//          SearchHit hit = new SearchHit(line, lineNumber);
//          response.addHit(hit);
//        }
//      }
//    } catch (IOException ioe) {
//      ioe.printStackTrace();
//    } finally {
//      if (response.getHits().size() > 0) {
//        listener.handleSearchResponse(response);
//        found++;
//      }
//      try {
//        reader.close();
//      } catch (Exception ignore) {}
//
//    }
//  }
//
//  private boolean lineContainsTerm(String line) {
//    if (regexSearchTerm) {
//      return searchTermPattern.matcher(line).lookingAt();
//    } else {
//      return line.contains(searchTerm);
//    }
//  }
//
//public void setShowResultList(boolean showResultList) {
//	this.showResultList = showResultList;
//}
//
//}
