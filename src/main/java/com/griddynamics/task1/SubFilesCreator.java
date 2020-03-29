package com.griddynamics.task1;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class SubFilesCreator {

    private static final int ONE_LINE = 1;
    public static final String SUB_FILE_PREFIX = "SubFile_";
    private static final Logger LOGGER = LogManager.getLogger(SubFilesCreator.class);

    public static void createSortedSubFiles(Path inputFilePath, int linesCountLimit, Path resultsFolderPath) throws FilesOperationException {
        validateInputParameters(inputFilePath, resultsFolderPath);

        long allowedMaxSubFileSize = MemoryUtils.getFreeMemorySize();
        LOGGER.info("Free memory: " + allowedMaxSubFileSize);

        String currentLineInSourceFile;
        int currentSubFileNumber = 1;
        long calculatedCurrentSizeLoaded = 0;
        List<String> linesWriteToSubFile = new ArrayList<>();
        linesWriteToSubFile.clear();

        try (BufferedReader sourceFileReader = Files.newBufferedReader(inputFilePath)) {
            while ((currentLineInSourceFile = sourceFileReader.readLine()) != null) {
                calculatedCurrentSizeLoaded += getSizeForLineLoaded(linesCountLimit, currentLineInSourceFile);
                if (isMaxAllowedSubFileSizeReached(linesCountLimit, allowedMaxSubFileSize, calculatedCurrentSizeLoaded)) {
                    linesWriteToSubFile.sort(String::compareTo);
                    writeLinesToSubFile(resultsFolderPath, currentSubFileNumber, linesWriteToSubFile);
                    linesWriteToSubFile.clear();
                    currentSubFileNumber += 1;
                    calculatedCurrentSizeLoaded = getSizeForLineLoaded(linesCountLimit, currentLineInSourceFile);
                }
                linesWriteToSubFile.add(currentLineInSourceFile);
                LOGGER.info("Currently loaded size: " + calculatedCurrentSizeLoaded);
            }
            if (!linesWriteToSubFile.isEmpty()) {
                linesWriteToSubFile.sort(String::compareTo);
                writeLinesToSubFile(resultsFolderPath, currentSubFileNumber, linesWriteToSubFile);
                linesWriteToSubFile.clear();
            }
        } catch (NoSuchFileException e) {
            throw new FilesOperationException("Input file " + inputFilePath + " not found", e.getCause());
        } catch (IOException e) {
            throw new FilesOperationException("Error during reading input file " + inputFilePath, e.getCause());
        }
    }

    private static void validateInputParameters(Path inputFilePath, Path resultsFolderPath) throws FilesOperationException {
        if (!Files.exists(inputFilePath)) {
            throw new FilesOperationException("Sorry, but your input file " + inputFilePath + " doesn't exist!");
        } else if (Files.isDirectory(inputFilePath)) {
            throw new FilesOperationException("Sorry, but your input file path " + inputFilePath + " isn't a file!");
        }
        try {
            if (!Files.exists(resultsFolderPath)) {
                Files.createTempDirectory(resultsFolderPath.toString());
            }
        } catch (IOException e) {
            throw new FilesOperationException("Error during creation of tmp folder " + resultsFolderPath, e.getCause());
        }
    }

    private static int getSizeForLineLoaded(int linesCountLimit, String line) {
        return (linesCountLimit < 1) ? MemoryUtils.getBytesCountInLine(line) : getLoadedLinesCount();
    }

    private static int getLoadedLinesCount() {
        return ONE_LINE;
    }

    public static String getSubFileNameByNumber(int subFileNumber) {
        return SubFilesCreator.SUB_FILE_PREFIX + subFileNumber;
    }

    private static boolean isMaxAllowedSubFileSizeReached(int linesCountLimit, long allowedMaxSubFileSize, long calculatedCurrentSubFileSize) {
        return (linesCountLimit < 1) ? calculatedCurrentSubFileSize > allowedMaxSubFileSize
                : calculatedCurrentSubFileSize > linesCountLimit;
    }

    private static void writeLinesToSubFile(Path resultsFolderPath, int subFileNumber, List<String> linesToWrite) throws FilesOperationException {
        Path pathToCurrentSubFile = Paths.get("");
        try {
            pathToCurrentSubFile = Paths.get(resultsFolderPath.toString(), getSubFileNameByNumber(subFileNumber));
            FileUtils.writeLines(pathToCurrentSubFile.toFile(), linesToWrite);
            LOGGER.info("Output file size: " + Files.size(pathToCurrentSubFile));
        } catch (IOException e) {
            throw new FilesOperationException("Error during writing to file " + pathToCurrentSubFile, e.getCause());
        }
    }
}
