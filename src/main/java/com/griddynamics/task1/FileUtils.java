package com.griddynamics.task1;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {

    public static String removeExtensionFromFilePath(String filePath) {
        return filePath.split("\\.")[0];
    }

    public static String getObjectName(String pathToSmth) {
        return FilenameUtils.getName(pathToSmth);
    }

    public static String getFilesExtensionInFolder(String folderPath) throws FilesOperationException {
        String extension = "";
        if(Files.isDirectory(Paths.get(folderPath))) {
            try (Stream<Path> allFilesInFolder = Files.list(Paths.get(folderPath))) {
                Optional<Path> firstFilePath = allFilesInFolder.findFirst();
                if (firstFilePath.isPresent()) {
                    String filePath = firstFilePath.get().toString();
                    extension = FilenameUtils.getExtension(filePath);
                }
            } catch (IOException e) {
                throw new FilesOperationException("Error during getting file extension in folder " + folderPath, e.getCause());
            }
        }
        return "." + extension;
    }

    public static long getFileSize(Path filePath) throws FilesOperationException {
        try {
            return Files.size(filePath);
        } catch (IOException e) {
            throw new FilesOperationException("Error during getting file size " + filePath.getFileName(), e.getCause());
        }
    }

    public static void writeLinesToFile(Collection<String> linesToWrite, Path filePath, OpenOption... writeOptions) throws FilesOperationException {
        try(BufferedWriter writer = Files.newBufferedWriter(filePath, writeOptions)) {
            for (String line : linesToWrite) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new FilesOperationException("Error during write to file " + filePath.getFileName(), e.getCause());
        }
    }

    public static void writeLineToFile(BufferedWriter writer, String lineToWrite) throws FilesOperationException {
        try {
            if(lineToWrite != null) {
                writer.write(lineToWrite);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new FilesOperationException("Error during write to file", e.getCause());
        }
    }

    public static void writeRemainingLinesToFile(BufferedWriter writer, BufferedReader reader) throws FilesOperationException {
        try {
            String lineToWrite;
            while((lineToWrite = reader.readLine()) != null){
                writer.write(lineToWrite);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new FilesOperationException("Error during write to file", e.getCause());
        }
    }

    public static void createFolderForSubFilesIfNotExists(String folderPath) throws FilesOperationException {
        Path pathToFolder = Paths.get(folderPath);
        if (!Files.exists(pathToFolder)) {
            try{
                Files.createDirectory(pathToFolder);
            } catch (IOException e) {
                throw new FilesOperationException("Error when try to create folder " + folderPath, e.getCause());
            }
        }
    }

    public static void removeAllFilesFromFolderIfFolderExists(String folderPath) throws FilesOperationException {
        Path pathToFolder = Paths.get(folderPath);
        if(Files.exists(pathToFolder) && Files.isDirectory(pathToFolder))
        {
            try(Stream<Path> files = Files.list(pathToFolder)) {
                files.forEach(filePath -> {
                    try {
                        Files.delete(filePath);
                    }   catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                throw new FilesOperationException("Error during removing files from folder " + folderPath, e.getCause());
            }
        }
    }

    public static void removeFolderAndSubFiles(String folderPath) throws FilesOperationException {
        removeAllFilesFromFolderIfFolderExists(folderPath);
        Path pathToFolder = Paths.get(folderPath);
        try {
            Files.deleteIfExists(pathToFolder);
        } catch (IOException e) {
            throw new FilesOperationException("Error during removing folder " + folderPath, e.getCause());
        }
    }

    public static void removeAllSimilarFoldersAndSubFiles(String folderPath) throws FilesOperationException {
        if(Files.isDirectory(Paths.get(folderPath))) {
            File folder = new File(folderPath);
            String parentFolderPath = folder.getParent();
            try (Stream<Path> allFilesInFolder = Files.list(Paths.get(parentFolderPath))) {
                Set<Path> foundFolder = allFilesInFolder.filter(path -> path.getFileName().toString().contains(folder.getName()))
                        .collect(Collectors.toSet());
                if(!foundFolder.isEmpty()) {
                    removeFolderAndSubFiles(foundFolder.toString());
                }
            } catch (IOException e) {
                throw new FilesOperationException("Error during getting file " + folderPath + " from folder " + folderPath, e.getCause());
            }
        }
    }

    public static long getFilesCountInFolder(String folderPath) throws FilesOperationException {
        long filesCount = 0;
        if(Files.isDirectory(Paths.get(folderPath))) {
            try (Stream<Path> allFilesInFolder = Files.list(Paths.get(folderPath))) {
                filesCount = allFilesInFolder.count();
            } catch (IOException e) {
                throw new FilesOperationException("Error during getting files count in folder " + folderPath, e.getCause());
            }
        }
        return filesCount;
    }

    public static String getFileInFolder(String folderPath, String fileNamePart) throws FilesOperationException {
        String pathToFile = "";
        if(Files.isDirectory(Paths.get(folderPath))) {
            try (Stream<Path> allFilesInFolder = Files.list(Paths.get(folderPath))) {
                Set<Path> foundFile = allFilesInFolder.filter(path -> path.getFileName().toString().contains(fileNamePart))
                                                        .collect(Collectors.toSet());
                if(!foundFile.isEmpty()) {
                    pathToFile = foundFile.toString();
                }
            } catch (IOException e) {
                throw new FilesOperationException("Error during getting file " + fileNamePart + " from folder " + folderPath, e.getCause());
            }
        }
        return pathToFile.replaceAll("]", "");
    }

    public static List<String> readAllFile(String filePath) throws FilesOperationException {
        List<String> allLines = new ArrayList<String>();
        String line;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))){
            while ((line = reader.readLine()) != null) {
                allLines.add(line);
            }
        } catch (IOException e) {
            throw new FilesOperationException("Error during reading file " + filePath, e.getCause());
        }
        return allLines;
    }

    public static boolean checkPathExists(String filePath) throws FilesOperationException {
        return Files.exists(Paths.get(filePath));
    }

    public static void copyFile(File file, File destFile) throws FilesOperationException {
        try {
            org.apache.commons.io.FileUtils.copyFile(file,destFile);
        } catch (IOException e) {
            throw new FilesOperationException("Error during copying file " + file.toString() + " to " + destFile.toString(), e.getCause());
        }
    }
}
