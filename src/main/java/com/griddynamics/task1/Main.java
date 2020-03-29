package com.griddynamics.task1;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static final Path SOURCE_FILE_PATH_SPLIT_BY_MEMORY_SIZE = Paths.get("Source_2mb.txt");
    private static final Path SOURCE_FILE_PATH_SPLIT_BY_LINES_COUNT = Paths.get("SourceFixed.txt");
    private static final Path RESULT_FILE_PATH = Paths.get("MergedResult.txt");
    private static final int ALLOWED_MAX_LINES_COUNT_TO_READ = 4;
    private static final String OPTION_SPLIT_BY_LINES = "-l";
    private static final String OPTION_SPLIT_BY_MEMORY = "-m";

    private static final Logger LOGGER = LogManager.getLogger(Main.class);


    public static void main(String[] args) {
        try {
            String option = args.length > 0 ? args[0] : OPTION_SPLIT_BY_LINES;
            switch (option) {
                case OPTION_SPLIT_BY_LINES:
                    runWithSplitByLinesCount();
                    break;
                case OPTION_SPLIT_BY_MEMORY:
                    runWithSplitByMemorySize();
                    break;
                default:
                    LOGGER.error("Unsupported option specified: " + option);
                    throw new IllegalArgumentException("Unsupported option specified: " + option);
            }
        } catch (FilesOperationException e) {
            LOGGER.error("There is an error during application execution: " + e.getMessage(), e);
        }
    }


    private static void runWithSplitByMemorySize() throws FilesOperationException {
        ExternalMergeSort.mergeSort(SOURCE_FILE_PATH_SPLIT_BY_MEMORY_SIZE, -1, RESULT_FILE_PATH);
    }

    private static void runWithSplitByLinesCount() throws FilesOperationException {
        ExternalMergeSort.mergeSort(SOURCE_FILE_PATH_SPLIT_BY_LINES_COUNT, ALLOWED_MAX_LINES_COUNT_TO_READ, RESULT_FILE_PATH);
    }


}
