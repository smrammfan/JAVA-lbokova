package com.griddynamics.task1;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Main {

    public static final String SOURCE_FILE_PATH_SPLIT_BY_MEMORY_SIZE = "Source_2mb.txt";
    public static final String SOURCE_FILE_PATH_SPLIT_BY_LINES_COUNT = "SourceFixed.txt";
    public static final int ALLOWED_MAX_LINES_COUNT_TO_READ = 4;
    public static final String OPTION_SPLIT_BY_LINES = "-l";
    public static final String OPTION_SPLIT_BY_MEMORY = "-m";

    private static final Logger LOGGER = LogManager.getLogger(Main.class);


    public static void main(String[] args) {
        try {
            String option = args.length > 0 ? args[0] : OPTION_SPLIT_BY_LINES;
            switch(option) {
                case OPTION_SPLIT_BY_LINES:
                    runWithSplitByLinesCount();
                    break;
                default:
                    runWithSplitByMemorySize();
                }
        } catch (FilesOperationException e) {
            LOGGER.error("There is an error during application execution: " + e.getMessage());
            if(e.getCause() != null) {
                LOGGER.error("Caused by: " + e.getCause().getStackTrace());
            }
        }
    }


    public static void runWithSplitByMemorySize() throws FilesOperationException {
        run(new SubFilesCreator(SOURCE_FILE_PATH_SPLIT_BY_MEMORY_SIZE));
    }

    public static void runWithSplitByLinesCount() throws FilesOperationException {
        run(new SubFilesCreator(SOURCE_FILE_PATH_SPLIT_BY_LINES_COUNT, ALLOWED_MAX_LINES_COUNT_TO_READ));
    }

    public static void run(SubFilesCreator subFilesCreator) throws FilesOperationException {
        String subFilesFolderPath = subFilesCreator.createSortedSubFiles();
        FilesMerger filesMerger = new FilesMerger(subFilesFolderPath);
        filesMerger.mergeSort();
    }


}
