package com.fdmgroup.cvgeneratorgradle.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FolderStructurePrinter {

    public static void main(String[] args) {
        folderStructurePrinter();
    }

    public static void folderStructurePrinter() {
        // Get the current directory
        Path currentDir = Paths.get("");
        try {
            // List all files and directories in the current directory
            Files.walk(currentDir)
                    .filter(Files::isRegularFile)
                    .forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printCurrentDirectory() {
        // Get the current working directory
        Path currentPath = Paths.get("").toAbsolutePath();
        System.out.println("Current working directory: " + currentPath);
    }

}