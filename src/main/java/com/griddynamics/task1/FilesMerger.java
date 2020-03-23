package com.griddynamics.task1;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class FilesMerger {

    private String resultFolderPath;
    private String resultFilePath;
    private String tmpResultFilePath;
    private String inputFilesFolderPath;
    private String prefixForMergedFolder;
    private static final Logger LOGGER = LogManager.getLogger(SubFilesCreator.class);

    public FilesMerger(String inputFilesFolderPath) {
        this.inputFilesFolderPath = inputFilesFolderPath;
        this.prefixForMergedFolder = this.inputFilesFolderPath + "_";
        this.resultFolderPath = inputFilesFolderPath + "_Merged";
    }

    public String mergeSort() throws FilesOperationException {
        FileUtils.removeAllSimilarFoldersAndSubFiles(this.prefixForMergedFolder);
        FileUtils.removeAllFilesFromFolderIfFolderExists(getResultFolderPath());
        FileUtils.createFolderForSubFilesIfNotExists(getResultFolderPath());
        LOGGER.info("Free memory: " + MemoryUtils.getFreeMemorySize());
        mergeFilesInFolder(this.inputFilesFolderPath, 1);
        return getResultFilePath();
    }

    public void mergeFilesInFolder(String inputFolderPath, int num) throws FilesOperationException {
        if(!Files.isDirectory(Paths.get(inputFolderPath))) {
            throw new FilesOperationException(inputFolderPath + " isn't directory!");
        }
        List<File> filesToMerge = (List<File>) org.apache.commons.io.FileUtils.listFiles(new File(inputFolderPath),null, false);
        if(filesToMerge.size() == 1) {
            FileUtils.copyFile(filesToMerge.get(0),new File(getResultFilePath()));
            return;
        }
        setTmpResultFolderPath(inputFolderPath + "_" + num);
        FileUtils.createFolderForSubFilesIfNotExists(getTmpResultFolderPath());

        for(int i=0; i+1<filesToMerge.size(); i=+2) {
            File file1 = filesToMerge.get(i);
            File file2 = filesToMerge.get(i+1);
            mergeFilesPairs(file1, file2);
        }
        if(filesToMerge.size() % 2 != 0) {
            File lastFile = filesToMerge.get(filesToMerge.size()-1);
            FileUtils.copyFile(lastFile,new File(getTmpResultFilePath(lastFile.getName())));
        }
        mergeFilesInFolder(getTmpResultFolderPath(), ++num);
    }

    private void mergeFilesPairs(File file1, File file2) throws FilesOperationException {
        String resultFile = getTmpResultFilePath(file1.getName());
        try(BufferedReader reader1 = Files.newBufferedReader(file1.toPath());
            BufferedReader reader2 = Files.newBufferedReader(file2.toPath());
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(resultFile)
                    , StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

            String line1 = reader1.readLine();
            String line2 = reader2.readLine();

            while(true) {
                if (line1 == null) {
                    FileUtils.writeLineToFile(writer, line2);
                    FileUtils.writeRemainingLinesToFile(writer,reader2);
                    break;
                } else if (line2 == null) {
                    FileUtils.writeLineToFile(writer, line1);
                    FileUtils.writeRemainingLinesToFile(writer,reader1);
                    break;
                } else if (line1.compareTo(line2) <= 0) {
                    FileUtils.writeLineToFile(writer, line1);
                    line1 = reader1.readLine();
                } else {
                    FileUtils.writeLineToFile(writer, line2);
                    line2 = reader2.readLine();
                }
            }

        } catch (IOException e) {
            throw new FilesOperationException("Error while writing to result file " + getResultFolderPath(), e.getCause());
        }
    }

    public String getResultFolderPath() {
        return this.resultFolderPath;
    }

    public String getTmpResultFolderPath() {
        return this.tmpResultFilePath;
    }

    public void setTmpResultFolderPath(String path) {
        this.tmpResultFilePath = path;
    }

    private String getMinLine(Collection<String> lines) {
        String minLine = null;
        for(String str: lines) {
            if(minLine == null || minLine.compareTo(str) > 0) {
                minLine = str;
            }
        }
        return minLine;
    }

    private String getResultFileName() {
       return FileUtils.getObjectName(this.inputFilesFolderPath);
    }

    private String getResultFilePath() throws FilesOperationException {
        return (this.resultFilePath == null) ? getResultFolderPath() + System.getProperty("file.separator") + getResultFileName()
                + FileUtils.getFilesExtensionInFolder(this.inputFilesFolderPath) : this.resultFilePath;
    }

    private String getTmpResultFilePath(String fileName) throws FilesOperationException {
        return getTmpResultFolderPath() + System.getProperty("file.separator") + fileName;
    }

    private BufferedReader getNeededReader(Map<BufferedReader, String> filesWithLines, String lastWrittenLine) {
        for(Map.Entry<BufferedReader, String> entry : filesWithLines.entrySet()) {
            if(entry.getValue().equals(lastWrittenLine)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void readNewLineFromFile(Map<BufferedReader, String> filesWithLines, String lastWrittenLine) throws FilesOperationException {
        BufferedReader reader = getNeededReader(filesWithLines, lastWrittenLine);
        String line;
        try {
            if((line = reader.readLine()) != null) {
                filesWithLines.put(reader, line);
            } else {
                filesWithLines.remove(reader);
            }
        } catch (IOException e) {
            throw new FilesOperationException("Error while reading new line from file", e.getCause());
        }
    }
}
