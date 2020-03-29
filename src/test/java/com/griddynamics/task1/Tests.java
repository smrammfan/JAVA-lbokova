package com.griddynamics.task1;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Tests {

    private List<Path> workingFolders = new ArrayList<>();
    private static final Logger LOGGER = LogManager.getLogger(SubFilesCreator.class);
    private static final List<String> EXPECTED_SORTED_LINES_1 = new ArrayList<>(Arrays.asList("222379", "358143", "610645", "620630"));
    private static final List<String> EXPECTED_SORTED_LINES_2 = new ArrayList<>(Arrays.asList("179393", "648156", "872412", "913859"));
    private static final List<String> EXPECTED_SORTED_LINES_3 = new ArrayList<>(Arrays.asList("224628", "411100"));
    private static final List<String> EXPECTED_MERGED_LINES_10 = new ArrayList<>(Arrays.asList("179393", "222379", "224628", "358143", "411100", "610645", "620630", "648156", "872412", "913859"));
    private static final String TEST_FILE1_NAME = "Test1.txt";
    private static final String FAKE_FILE_NAME = "fakeFile.txt";
    private static final String FAKE_FOLDER_NAME = "fakeFolder";
    private static final Path RESULT_FILE_PATH = Paths.get("ResultTestMerge.txt");
    private static final Path RESULT_FOLDER_PATH = Paths.get("ResultTestMerge");

    @Before
    public void beforeScenarios() {
        workingFolders.clear();
        LOGGER.info("Variables cleared");
    }

    @Test(expected = FilesOperationException.class)
    public void shouldThrowExceptionIfFileNotFoundForMergeSort() throws FilesOperationException {
        ExternalMergeSort.mergeSort(Paths.get(FAKE_FILE_NAME), 0, RESULT_FILE_PATH);
    }

    @Test(expected = FilesOperationException.class)
    public void shouldThrowExceptionIfNotFileForMergeSort() throws FilesOperationException {
        ExternalMergeSort.mergeSort(Paths.get(FAKE_FOLDER_NAME), 0, RESULT_FILE_PATH);
        workingFolders.add(Paths.get(FAKE_FOLDER_NAME));
    }

    @Test(expected = FilesOperationException.class)
    public void shouldThrowExceptionIfFileNotFoundForFileSplit() throws FilesOperationException {
        SubFilesCreator.createSortedSubFiles(Paths.get(FAKE_FOLDER_NAME), 0, RESULT_FOLDER_PATH);
        workingFolders.add(Paths.get(FAKE_FOLDER_NAME));
        workingFolders.add(RESULT_FOLDER_PATH);
    }

    @Test(expected = FilesOperationException.class)
    public void shouldThrowExceptionIfNotFileForFileSplit() throws FilesOperationException {
        SubFilesCreator.createSortedSubFiles(Paths.get(FAKE_FOLDER_NAME), 0, RESULT_FOLDER_PATH);
        workingFolders.add(Paths.get(FAKE_FOLDER_NAME));
        workingFolders.add(RESULT_FOLDER_PATH);
    }

    @Test
    public void correctSubFilesNumberCreatedIfFileIsNotSmall() throws FilesOperationException, IOException {
        String testFilePath = this.getClass().getClassLoader().getResource(TEST_FILE1_NAME).getPath();
        SubFilesCreator.createSortedSubFiles(Paths.get(testFilePath), 4, RESULT_FOLDER_PATH);
        workingFolders.add(RESULT_FOLDER_PATH);
        assertThat(Files.exists(RESULT_FOLDER_PATH)).as("Folder for sub files wasn't created!").isEqualTo(true);
        assertThat(getFilesCountInFolder(RESULT_FOLDER_PATH)).as("Wrong number files was created!").isEqualTo(3);
    }

    @Test
    public void correctSubFilesNumberCreatedIfFileIsSmall() throws FilesOperationException, IOException {
        String testFilePath = this.getClass().getClassLoader().getResource(TEST_FILE1_NAME).getPath();
        SubFilesCreator.createSortedSubFiles(Paths.get(testFilePath), 10, RESULT_FOLDER_PATH);
        workingFolders.add(RESULT_FOLDER_PATH);
        assertThat(Files.exists(RESULT_FOLDER_PATH)).as("Folder for sub files wasn't created!").isEqualTo(true);
        assertThat(getFilesCountInFolder(RESULT_FOLDER_PATH)).as("Wrong number files was created!").isEqualTo(1);
    }

    @Test
    public void checkCorrectSortedContentOfSubFilesIfInputFileIsNotSmall() throws FilesOperationException, IOException {
        String testFilePath = this.getClass().getClassLoader().getResource(TEST_FILE1_NAME).getPath();
        SubFilesCreator.createSortedSubFiles(Paths.get(testFilePath), 4, RESULT_FOLDER_PATH);
        workingFolders.add(RESULT_FOLDER_PATH);
        Path oneSubFile = Paths.get(RESULT_FOLDER_PATH.toString(), SubFilesCreator.getSubFileNameByNumber(1));
        //file #1
        List<String> allLines = FileUtils.readLines(oneSubFile.toFile());
        assertThat(allLines.size()).as("Wrong number lines in file" + oneSubFile + "!").isEqualTo(EXPECTED_SORTED_LINES_1.size());
        assertThat(allLines).as("Wrong order of lines in file" + oneSubFile + "!").isEqualTo(EXPECTED_SORTED_LINES_1);
        allLines.clear();
        //file #2
        oneSubFile = Paths.get(RESULT_FOLDER_PATH.toString(), SubFilesCreator.getSubFileNameByNumber(2));
        allLines = FileUtils.readLines(oneSubFile.toFile());
        assertThat(allLines.size()).as("Wrong number lines in file" + oneSubFile + "!").isEqualTo(EXPECTED_SORTED_LINES_2.size());
        assertThat(allLines).as("Wrong order of lines in file" + oneSubFile + "!").isEqualTo(EXPECTED_SORTED_LINES_2);
        allLines.clear();
        //file #3
        oneSubFile = Paths.get(RESULT_FOLDER_PATH.toString(), SubFilesCreator.getSubFileNameByNumber(3));
        allLines = FileUtils.readLines(oneSubFile.toFile());
        assertThat(allLines.size()).as("Wrong number lines in file" + oneSubFile + "!").isEqualTo(EXPECTED_SORTED_LINES_3.size());
        assertThat(allLines).as("Wrong order of lines in file" + oneSubFile + "!").isEqualTo(EXPECTED_SORTED_LINES_3);
        allLines.clear();
    }

    @Test
    public void checkCorrectSortedContentOfSubFilesIfInputFileIsSmall() throws FilesOperationException, IOException {
        String testFilePath = this.getClass().getClassLoader().getResource(TEST_FILE1_NAME).getPath();
        SubFilesCreator.createSortedSubFiles(Paths.get(testFilePath), 4, RESULT_FOLDER_PATH);
        workingFolders.add(RESULT_FOLDER_PATH);
        Path oneSubFile = Paths.get(RESULT_FOLDER_PATH.toString(), SubFilesCreator.getSubFileNameByNumber(1));
        //file #1
        List<String> allLines = FileUtils.readLines(oneSubFile.toFile());
        assertThat(allLines.size()).as("Wrong number lines in file" + oneSubFile + "!").isEqualTo(EXPECTED_SORTED_LINES_1.size());
        assertThat(allLines).as("Wrong order of lines in file" + oneSubFile + "!").isEqualTo(EXPECTED_SORTED_LINES_1);
    }


    @Test
    public void checkThatFilesMergeCorrectlyMergeTwoFiles() throws FilesOperationException, IOException {
        String testFilePath = this.getClass().getClassLoader().getResource(TEST_FILE1_NAME).getPath();
        ExternalMergeSort.mergeSort(Paths.get(testFilePath), 6, RESULT_FILE_PATH);
        List<String> allLines = FileUtils.readLines(RESULT_FILE_PATH.toFile());
        assertThat(allLines.size()).as("Wrong number lines in file" + RESULT_FILE_PATH + "!").isEqualTo(10);
        assertThat(allLines).as("Wrong order of lines in file" + RESULT_FILE_PATH + "!").isEqualTo(EXPECTED_MERGED_LINES_10);
    }

    @Test
    public void checkThatFilesMergeCorrectlyMergeThreeFiles() throws FilesOperationException, IOException {
        String testFilePath = this.getClass().getClassLoader().getResource(TEST_FILE1_NAME).getPath();
        ExternalMergeSort.mergeSort(Paths.get(testFilePath), 4, RESULT_FILE_PATH);
        List<String> allLines = FileUtils.readLines(RESULT_FILE_PATH.toFile());
        assertThat(allLines.size()).as("Wrong number lines in file" + RESULT_FILE_PATH + "!").isEqualTo(10);
        assertThat(allLines).as("Wrong order of lines in file" + RESULT_FILE_PATH + "!").isEqualTo(EXPECTED_MERGED_LINES_10);
    }


    @After
    public void AfterScenarios() {
        workingFolders.forEach(folder -> {
            try {
                FileUtils.deleteDirectory(folder.toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        try {
            Files.deleteIfExists(RESULT_FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info("Files removed");
    }

    public static long getFilesCountInFolder(Path folderPath) throws FilesOperationException {
        long filesCount = 0;
        if (Files.isDirectory(folderPath)) {
            try (Stream<Path> allFilesInFolder = Files.list(folderPath)) {
                filesCount = allFilesInFolder.count();
            } catch (IOException e) {
                throw new FilesOperationException("Error during getting files count in folder " + folderPath, e.getCause());
            }
        }
        return filesCount;
    }
}
