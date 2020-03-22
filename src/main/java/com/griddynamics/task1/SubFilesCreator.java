package com.griddynamics.task1;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class SubFilesCreator {

    private static final int NOT_DEFINED = -1;
    private static final int ONE_LINE = 1;
    private static final Logger LOGGER = LogManager.getLogger(SubFilesCreator.class);


    private String sourceFilePath;
    private String resultsFolderPath;
    private long allowedMaxSubFileSize;
    private int allowedMaxLinesCountToRead;
    private List<Path> pathsToCreatedSubFiles;


    public SubFilesCreator(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
        this.allowedMaxSubFileSize = MemoryUtils.getFreeMemorySize();
        this.allowedMaxLinesCountToRead = NOT_DEFINED;
        this.pathsToCreatedSubFiles = new ArrayList<>();
        this.resultsFolderPath = buildFolderPathForSubFiles();
    }

    public SubFilesCreator(String sourceFilePath, int allowedMaxLinesCountToRead) {
        this.sourceFilePath = sourceFilePath;
        this.allowedMaxSubFileSize = NOT_DEFINED;
        this.allowedMaxLinesCountToRead = allowedMaxLinesCountToRead;
        this.pathsToCreatedSubFiles = new ArrayList<>();
        this.resultsFolderPath = buildFolderPathForSubFiles();
    }

    public String createSortedSubFiles() throws FilesOperationException {
        FileUtils.removeAllFilesFromFolderIfFolderExists(this.resultsFolderPath);
        FileUtils.createFolderForSubFilesIfNotExists(this.resultsFolderPath);
        LOGGER.info("Free memory: " + MemoryUtils.getFreeMemorySize());

        String currentLineInSourceFile;
        int currentSubFileNumber = 1;
        long calculatedCurrentSizeLoaded = 0;
        List<String> linesWriteToSubFile = new ArrayList<>();
        linesWriteToSubFile.clear();

        try(BufferedReader sourceFileReader = Files.newBufferedReader(Paths.get(this.sourceFilePath))) {
            while((currentLineInSourceFile = sourceFileReader.readLine()) != null) {
                calculatedCurrentSizeLoaded += getSizeForLineLoaded(currentLineInSourceFile);
                if(isMaxAllowedSubFileSizeReached(calculatedCurrentSizeLoaded)) {
                    linesWriteToSubFile.sort(String::compareTo);
                    writeLinesToSubFile(currentSubFileNumber,linesWriteToSubFile);
                    linesWriteToSubFile.clear();
                    currentSubFileNumber += 1;
                    calculatedCurrentSizeLoaded = getSizeForLineLoaded(currentLineInSourceFile);
                }
                linesWriteToSubFile.add(currentLineInSourceFile);
                LOGGER.info("Currently loaded size: " + calculatedCurrentSizeLoaded);
            }
            if(!linesWriteToSubFile.isEmpty()) {
                writeLinesToSubFile(currentSubFileNumber,linesWriteToSubFile);
                linesWriteToSubFile.clear();
            }
        } catch (NoSuchFileException e) {
            throw new FilesOperationException("Input file " + this.sourceFilePath + " not found", e.getCause());
        } catch (IOException e) {
            throw new FilesOperationException("Error during reading input file " + this.sourceFilePath, e.getCause());
        }
        return this.resultsFolderPath;
    }

    private int getSizeForLineLoaded(String line) {
        return (this.allowedMaxLinesCountToRead == NOT_DEFINED) ? MemoryUtils.getBytesCountInLine(line) : getLoadedLinesCount();
    }

    private int getLoadedLinesCount() {
        return ONE_LINE;
    }

     private boolean isMaxAllowedSubFileSizeReached(long calculatedCurrentSubFileSize) {
         return (this.allowedMaxLinesCountToRead == NOT_DEFINED) ? calculatedCurrentSubFileSize > this.allowedMaxSubFileSize
                                                                : calculatedCurrentSubFileSize > this.allowedMaxLinesCountToRead;
     }

     private void writeLinesToSubFile(int subFileNumber, List<String> linesToWrite) throws FilesOperationException {
         Path pathToCurrentSubFile = Paths.get(buildPathToSubFile(subFileNumber));
         this.pathsToCreatedSubFiles.add(pathToCurrentSubFile);
         FileUtils.writeLinesToFile(linesToWrite, pathToCurrentSubFile, StandardOpenOption.WRITE
                                    , StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
         LOGGER.info("Output file size: " + FileUtils.getFileSize(pathToCurrentSubFile));
     }

     private String buildPathToSubFile(int subFileNumber) {
        return this.resultsFolderPath + System.getProperty("file.separator") + subFileNumber + "_"
                                        + FileUtils.getObjectName(this.sourceFilePath);
     }

     private String buildFolderPathForSubFiles() {
        return FileUtils.removeExtensionFromFilePath(this.sourceFilePath);
     }
}
