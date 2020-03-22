package com.griddynamics.task1;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Stream;

public class FilesMerger {

    private String resultFolderPath;
    private String resultFilePath;
    private String inputFilesFolderPath;
    private static final Logger LOGGER = LogManager.getLogger(SubFilesCreator.class);

    public FilesMerger(String inputFilesFolderPath) {
        this.inputFilesFolderPath = inputFilesFolderPath;
        this.resultFolderPath = inputFilesFolderPath + "_Merged";
    }

    public String mergeFiles() throws FilesOperationException {
        FileUtils.removeAllFilesFromFolderIfFolderExists(getResultFolderPath());
        FileUtils.createFolderForSubFilesIfNotExists(getResultFolderPath());
        LOGGER.info("Free memory: " + MemoryUtils.getFreeMemorySize());

        Map<BufferedReader,String> readersWithLines = new HashMap<>();
        try(Stream<Path> filesToMerge = Files.list(Paths.get(this.inputFilesFolderPath))) {
            filesToMerge.forEach(path -> {
                try {
                    BufferedReader reader = Files.newBufferedReader(path);
                    String line = reader.readLine();
                    if(line != null) {
                        readersWithLines.put(reader, line);
                    }
                } catch (IOException e) {
                    LOGGER.error("Error while reading the first line from file " + path);
                    e.printStackTrace();
                }

            });
        } catch (IOException e) {
            throw new FilesOperationException("Error while reading files to merge from folder " + this.inputFilesFolderPath, e.getCause());
        }

        Set<BufferedReader> allReaders = readersWithLines.keySet();

        String minLine = "";
        try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(getResultFilePath())
                , StandardOpenOption.WRITE, StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
            while(!readersWithLines.isEmpty()) {
                minLine = getMinLine(readersWithLines.values());
                FileUtils.writeLineToFile(writer, minLine);
                readNewLineFromFile(readersWithLines, minLine);
            }

        } catch (IOException e) {
            throw new FilesOperationException("Error while writing to result file " + getResultFolderPath(), e.getCause());
        } finally {
            for (BufferedReader reader : allReaders) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("Error while closing reader");
                    e.printStackTrace();
                }
            }
        }
        return getResultFilePath();

    }

    public String getResultFolderPath() {
        return this.resultFolderPath;
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
