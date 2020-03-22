package com.griddynamics.task1;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tests {

    private List<String> workingFolders = new ArrayList<>();
    private static final Logger LOGGER = LogManager.getLogger(SubFilesCreator.class);
    private static final List<String> EXPECTED_SORTED_LINES_1 = new ArrayList<>(Arrays.asList("222379", "358143", "610645", "620630"));
    private static final List<String> EXPECTED_SORTED_LINES_2 = new ArrayList<>(Arrays.asList("179393", "648156", "872412", "913859"));
    private static final List<String> EXPECTED_SORTED_LINES_3 = new ArrayList<>(Arrays.asList("224628", "411100"));
    private static final List<String> EXPECTED_MERGED_LINES = new ArrayList<>(Arrays.asList("222379", "610645", "620630", "648156", "872412", "913859"));
    private static final String TEST_FOLDER_WITH_RESULTS = "target/test-classes/";
    private static final String TEST_FILE1_NAME = "Test1.txt";
    private static final String TEST_FILE2_FOLDER_NAME = "Test2";
    private static final String FAKE_FILE_NAME = "fakeFile.txt";
    private static final String FAKE_FOLDER_NAME = "fakeFolder";

    @Before
    public void beforeScenarios() {
        workingFolders.clear();
        LOGGER.info("Variables cleared");
    }

    @Test(expected = FilesOperationException.class)
    public void shouldThrowExceptionIfFileNotFound() throws FilesOperationException {
        Main.run(new SubFilesCreator(FAKE_FILE_NAME));
    }

    @Test(expected = FilesOperationException.class)
    public void shouldThrowExceptionIfNotFile() throws FilesOperationException {
        workingFolders.add(FAKE_FOLDER_NAME);
        Main.run(new SubFilesCreator(FAKE_FOLDER_NAME));
    }

    @Test
    public void correctSubFilesNumberCreatedIfFileIsNotSmall() throws FilesOperationException {
        String testFilePath = this.getClass().getClassLoader().getResource(TEST_FILE1_NAME).getPath();
        SubFilesCreator subFilesCreator = new SubFilesCreator(testFilePath, 4);
        String subFilesFolder = subFilesCreator.createSortedSubFiles();
        workingFolders.add(subFilesFolder);
        assertThat(FileUtils.checkPathExists(subFilesFolder)).as("Folder for sub files wasn't created!").isEqualTo(true);
        assertThat(FileUtils.getFilesCountInFolder(subFilesFolder)).as("Wrong number files was created!").isEqualTo(3);
    }

    @Test
    public void correctSubFilesNumberCreatedIfFileIsSmall() throws FilesOperationException {
        String testFilePath = this.getClass().getClassLoader().getResource(TEST_FILE1_NAME).getPath();
        SubFilesCreator subFilesCreator = new SubFilesCreator(testFilePath, 10);
        String subFilesFolder = subFilesCreator.createSortedSubFiles();
        workingFolders.add(subFilesFolder);
        assertThat(FileUtils.checkPathExists(subFilesFolder)).as("Folder for sub files wasn't created!").isEqualTo(true);
        assertThat(FileUtils.getFilesCountInFolder(subFilesFolder)).as("Wrong number files was created!").isEqualTo(1);
    }

    @Test
    public void checkCorrectSortedContentOfSubFilesIfInputFileIsNotSmall() throws FilesOperationException {
        String testFilePath = this.getClass().getClassLoader().getResource(TEST_FILE1_NAME).getPath();
        SubFilesCreator subFilesCreator = new SubFilesCreator(testFilePath, 4);
        String subFilesFolder = subFilesCreator.createSortedSubFiles();
        workingFolders.add(subFilesFolder);
        String filePath = FileUtils.getFileInFolder(subFilesFolder, "1_");
        //file #1
        List<String> allLines = FileUtils.readAllFile(buildTestResultFilePath(subFilesFolder, filePath));
        assertThat(allLines.size()).as("Wrong number lines in file" + filePath + "!").isEqualTo(EXPECTED_SORTED_LINES_1.size());
        assertThat(allLines).as("Wrong order of lines in file" + filePath + "!").isEqualTo(EXPECTED_SORTED_LINES_1);
        allLines.clear();
        //file #2
        filePath = FileUtils.getFileInFolder(subFilesFolder, "2_");
        allLines = FileUtils.readAllFile(buildTestResultFilePath(subFilesFolder, filePath));
        assertThat(allLines.size()).as("Wrong number lines in file" + filePath + "!").isEqualTo(EXPECTED_SORTED_LINES_2.size());
        assertThat(allLines).as("Wrong order of lines in file" + filePath + "!").isEqualTo(EXPECTED_SORTED_LINES_2);
        allLines.clear();
        //file #3
        filePath = FileUtils.getFileInFolder(subFilesFolder, "3_");
        allLines = FileUtils.readAllFile(buildTestResultFilePath(subFilesFolder, filePath));
        assertThat(allLines.size()).as("Wrong number lines in file" + filePath + "!").isEqualTo(EXPECTED_SORTED_LINES_3.size());
        assertThat(allLines).as("Wrong order of lines in file" + filePath + "!").isEqualTo(EXPECTED_SORTED_LINES_3);
        allLines.clear();

    }

    @Test
    public void checkCorrectSortedContentOfSubFilesIfInputFileIsSmall() throws FilesOperationException {
        String testFilePath = this.getClass().getClassLoader().getResource(TEST_FILE1_NAME).getPath();
        SubFilesCreator subFilesCreator = new SubFilesCreator(testFilePath, 4);
        String subFilesFolder = subFilesCreator.createSortedSubFiles();
        workingFolders.add(subFilesFolder);
        String filePath = FileUtils.getFileInFolder(subFilesFolder, "1_");
        //file #1
        List<String> allLines = FileUtils.readAllFile(buildTestResultFilePath(subFilesFolder, filePath));
        assertThat(allLines.size()).as("Wrong number lines in file" + filePath + "!").isEqualTo(EXPECTED_SORTED_LINES_1.size());
        assertThat(allLines).as("Wrong order of lines in file" + filePath + "!").isEqualTo(EXPECTED_SORTED_LINES_1);
    }

    //// FilesMerger tests

    @Test(expected = FilesOperationException.class)
    public void filesMergerShouldThrowExceptionIfFolderNotFound() throws FilesOperationException {
        FilesMerger filesMerger = new FilesMerger(FAKE_FOLDER_NAME);
        workingFolders.add(FAKE_FOLDER_NAME);
        filesMerger.mergeFiles();
    }


    @Test(expected = FilesOperationException.class)
    public void filesMergerShouldThrowExceptionIfInputParamNotFolder() throws FilesOperationException {
        FilesMerger filesMerger = new FilesMerger(FAKE_FILE_NAME);
        workingFolders.add(FAKE_FILE_NAME);
        filesMerger.mergeFiles();
    }

    @Test
    public void checkThatFilesMergeCorrectlyMergeTwoFiles() throws FilesOperationException {
        String testSubFilesFolderPath = this.getClass().getClassLoader().getResource(TEST_FILE2_FOLDER_NAME).getPath();
        FilesMerger filesMerger = new FilesMerger(testSubFilesFolderPath);
        String resultFilePath = filesMerger.mergeFiles();
        workingFolders.add(filesMerger.getResultFolderPath());
        List<String> allLines = FileUtils.readAllFile(resultFilePath);
        assertThat(allLines.size()).as("Wrong number lines in file" + resultFilePath + "!").isEqualTo(6);
        assertThat(allLines).as("Wrong order of lines in file" + resultFilePath + "!").isEqualTo(EXPECTED_MERGED_LINES);
    }



    @After
    public void AfterScenarios()  throws FilesOperationException{
        workingFolders.forEach(folder -> {
            try {
                FileUtils.removeFolderAndSubFiles(folder);
            } catch (FilesOperationException e) {
                e.printStackTrace();
            }
        });
        LOGGER.info("Files removed");
    }

    private String buildTestResultFilePath(String folderPath, String filePath) {
        return TEST_FOLDER_WITH_RESULTS + FileUtils.getObjectName(folderPath)
                                        + System.getProperty("file.separator")
                                        + FileUtils.getObjectName(filePath);
    }
}
