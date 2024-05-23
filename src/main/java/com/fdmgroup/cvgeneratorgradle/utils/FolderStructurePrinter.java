package com.fdmgroup.cvgeneratorgradle.utils;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

@Getter
@Setter
public class FolderStructurePrinter {
    private int fileCount = 0;

    public static List<Path> folderStructurePrinter(String path) {
        // Get the current directory
        List<Path> ret = new ArrayList<>();
        try (Stream<Path> files = Files.walk(Paths.get(path))) {
            // List all files and directories in the current directory
            files
                    .filter(Files::isRegularFile)
                    .forEach(ret::add);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public Map<String, String> listFilesUsingFileWalkAndVisitor(String dir) throws IOException {
        Map<String,String> filesSorted = new TreeMap<>(Collections.reverseOrder());
        Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (!Files.isDirectory(file)) {
                    filesSorted.put(attrs.lastModifiedTime().toString(),file.getFileName().toString());

                    //System.out.println("attr: " + attrs.creationTime());
                    fileCount++;
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return filesSorted;
    }
}