package com.fdmgroup.cvgeneratorgradle.utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;


import java.nio.file.Files;
import java.nio.file.Path;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class SaveObjectToJson {
    public static TreeMap<String, String> recentFiles = new TreeMap<>();
    public static Set<String> recentFileNames = new HashSet<>();

    static String savePath = System.getProperty("user.home") +
                    "/cvGeneratorSaves";

    public static void saveObjectAsJson(Object object) {
        doSave(object, "", true);
    }

    private static void doSave(Object object, String fileName, boolean isAutoSave) {

        String fileNameWODir = fileName;
        String directory = "";
        if (!isAutoSave) {
            for (int i = fileName.length() - 1; i >= 0; i--) {
                if (fileName.charAt(i) == File.pathSeparatorChar) {
                    fileNameWODir = fileName.substring(i + 1);
                    directory = fileName.substring(0, i + 1);
                    break;
                }
            }
        } else {
            directory = savePath + "/auto saves/";
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(object);
        /*System.out.println("without dir: "+ fileNameWODir);
        System.out.println("dir name: "+ directory);*/
        File newFile = new File(directory);
        if (!newFile.exists()) {
            if (newFile.mkdirs()) System.out.println(newFile + " created");
        }
        if (isAutoSave) {
            FolderStructurePrinter folderStructurePrinter = new FolderStructurePrinter();
            TreeMap<String, String> autoSaves;
            try {
                autoSaves = (TreeMap<String, String>) folderStructurePrinter.listFilesUsingFileWalkAndVisitor(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (autoSaves.size() < 5) {
                fileNameWODir = "autosave" + (autoSaves.size() + 1) + "_part.json";
            } else {
                fileNameWODir = autoSaves.lastEntry().getValue();
            }
            System.out.println(fileNameWODir);
        }
        //fileNameWODir = "autosave_part.json";
        File newFileWithDir = new File(directory + fileNameWODir);
        try (FileWriter fw = new FileWriter(newFileWithDir);) {
            System.out.println(newFileWithDir);
            fw.write(json);
            if (recentFileNames.add(newFileWithDir.getAbsolutePath())) {
                if (recentFiles.size() < 10) {
                    recentFiles.putIfAbsent(String.valueOf(Files.getLastModifiedTime(Path.of(newFileWithDir.getAbsolutePath()))), newFileWithDir.getAbsolutePath());
                } else {
                    recentFiles.pollLastEntry();
                    //recentFiles.remove(recentFiles.lastEntry());
                    recentFiles.putIfAbsent(String.valueOf(Files.getLastModifiedTime(Path.of(newFileWithDir.getAbsolutePath()))), newFileWithDir.getAbsolutePath());
                }
            } else {
                AtomicReference<String> toRemove = new AtomicReference<>("");
                recentFiles.forEach((key, value) -> {
                    if (value.equals(newFileWithDir.getAbsolutePath())) {
                        toRemove.set(key);
                    }
                });

                try {
                    recentFiles.remove(toRemove.get());
                    System.out.println(toRemove.get());
                    recentFiles.put(String.valueOf(Files.getLastModifiedTime(Path.of(newFileWithDir.getAbsolutePath()))), newFileWithDir.getAbsolutePath());
                    //System.out.println(recentFiles);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            //recentFiles.forEach((k,v)-> System.out.println(v));

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        System.out.println(object.getClass().getSimpleName() + " saved as JSON");
    }

    public static void saveObjectAsJson(Object object, String fileName) {
        doSave(object, fileName, false);

        // create file path with user and his local documents folder e.g. C:\Users\Username\Documents
        //String documentsFolderPath = System.getProperty("user.home") + File.separator + "Documents";
        //String filePath = documentsFolderPath + File.separator + "CvAutoSave.json";
        // create json locally
        /*try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }*/
    }

    public static File selectFileFromFileChooser(String fileChooserTitle, String outputDescription, String fileExtension, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(fileChooserTitle);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(outputDescription, fileExtension));
        return fileChooser.showSaveDialog(stage);
    }

}
