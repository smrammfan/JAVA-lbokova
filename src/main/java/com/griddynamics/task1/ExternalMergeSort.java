package com.griddynamics.task1;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Random;

public class ExternalMergeSort {

    public static void mergeSort(Path inputFilePath, int linesCountLimit, Path resultFilePath) throws FilesOperationException {
        if (!Files.exists(inputFilePath)) {
            throw new FilesOperationException("Sorry, but your input file " + inputFilePath + " doesn't exist!");
        } else if (Files.isDirectory(inputFilePath)) {
            throw new FilesOperationException("Sorry, but your input file path " + inputFilePath + " isn't a file!");
        }
        Path tmpWorkFolder = Paths.get("");
        try {
            tmpWorkFolder = Files.createTempDirectory("MyFilesMerger");
            SubFilesCreator.createSortedSubFiles(inputFilePath, linesCountLimit, tmpWorkFolder);
            File mergedResultFile = mergeFilesInFolder(tmpWorkFolder);
            FileUtils.copyFile(mergedResultFile, resultFilePath.toFile());
            Files.delete(mergedResultFile.toPath());
            Files.delete(tmpWorkFolder);
        } catch (IOException e) {
            throw new FilesOperationException("Error while work with tmp directory " + tmpWorkFolder.toString(), e.getCause());
        }
    }

    private static File mergeFilesInFolder(Path inputFolderPath) throws FilesOperationException {
        if (!Files.isDirectory(inputFolderPath)) {
            throw new FilesOperationException(inputFolderPath + " isn't directory!");
        }
        List<File> filesToMerge = (List<File>) FileUtils.listFiles(new File(inputFolderPath.toUri()), null, false);
        if (filesToMerge.size() == 1) {
            return filesToMerge.get(0);
        }

        for (int i = 0; i + 1 < filesToMerge.size(); i = +2) {
            File file1 = filesToMerge.get(i);
            File file2 = filesToMerge.get(i + 1);
            mergeFilesPairs(file1, file2);
            file1.delete();
            file2.delete();
        }
        return mergeFilesInFolder(inputFolderPath);
    }

    private static void mergeFilesPairs(File file1, File file2) throws FilesOperationException {
        String mergeResultFileName = "Result" + new Random().nextInt(1000) + ".txt";
        Path mergeResultFilePath = Paths.get(file1.getParent(), mergeResultFileName);
        try (BufferedReader reader1 = Files.newBufferedReader(file1.toPath());
             BufferedReader reader2 = Files.newBufferedReader(file2.toPath());
             BufferedWriter writer = Files.newBufferedWriter(mergeResultFilePath
                     , StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

            String line1 = reader1.readLine();
            String line2 = reader2.readLine();

            while (true) {
                if (line1 == null) {
                    writeLineToFile(writer, line2);
                    writeRemainingLinesToFile(writer, reader2);
                    break;
                } else if (line2 == null) {
                    writeLineToFile(writer, line1);
                    writeRemainingLinesToFile(writer, reader1);
                    break;
                } else if (line1.compareTo(line2) <= 0) {
                    writeLineToFile(writer, line1);
                    line1 = reader1.readLine();
                } else {
                    writeLineToFile(writer, line2);
                    line2 = reader2.readLine();
                }
            }

        } catch (IOException e) {
            throw new FilesOperationException("Error while writing to merge result file " + mergeResultFilePath.toString(), e.getCause());
        }
    }

    private static void writeLineToFile(BufferedWriter writer, String lineToWrite) throws IOException {
        if(writer == null) {
            throw new IllegalArgumentException("Writer shouldn't be null!");
        }
        if (lineToWrite != null) {
            writer.write(lineToWrite);
            writer.newLine();
        }
    }

    private static void writeRemainingLinesToFile(BufferedWriter writer, BufferedReader reader) throws IOException {
        if(writer == null) {
            throw new IllegalArgumentException("Writer shouldn't be null!");
        }
        if(reader == null) {
            throw new IllegalArgumentException("Reader shouldn't be null!");
        }
        String lineToWrite;
        while ((lineToWrite = reader.readLine()) != null) {
            writer.write(lineToWrite);
            writer.newLine();
        }
    }
}
