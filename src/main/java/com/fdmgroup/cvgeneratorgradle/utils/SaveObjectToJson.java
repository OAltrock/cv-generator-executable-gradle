package com.fdmgroup.cvgeneratorgradle.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SaveObjectToJson {
    //static final String AUTO_SAVE_PATH = "./saves/autosave_part.json";

    public static void saveObjectAsJson(Object object) {
        doSave(object, "",true);
    }

    private static void doSave(Object object, String fileName, boolean isAutoSave) {
        String fileNameWODir = fileName;
        String directory = "";
        if (!isAutoSave) {
            for (int i = fileName.length() - 1; i >= 0; i--) {
                if (fileName.charAt(i) == '/') {
                    fileNameWODir = fileName.substring(i + 1);
                    directory = fileName.substring(0, i + 1);
                    break;
                }
            }
        }
        else {
            directory = "./saves/";
            fileNameWODir = "autosave_part.json";
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(object);
        /*System.out.println("without dir: "+ fileNameWODir);
        System.out.println("dir name: "+ directory);*/
        File newFile = new File(directory);
        if (!newFile.exists()) {
            newFile.mkdirs();
        }
        File newFileWithDir = new File(directory + fileNameWODir);
        try (FileWriter fw = new FileWriter(newFileWithDir);) {
            fw.write(json);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        System.out.println(object.getClass().getSimpleName() + " saved as JSON");
    }

    public static void saveObjectAsJson(Object object, String fileName) {
        doSave(object,fileName,false);
        //create directory if it doesn't exist



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

}
