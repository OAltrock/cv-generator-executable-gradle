package com.fdmgroup.cvgeneratorgradle.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SaveObjectToJson {
    public static void saveObjectAsJson(Object object, String fileName) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(object);

        // create file path with user and his local documents folder e.g. C:\Users\Username\Documents
        //String documentsFolderPath = System.getProperty("user.home") + File.separator + "Documents";
        //String filePath = documentsFolderPath + File.separator + "CvAutoSave.json";
        System.out.println(fileName);

        // create json locally
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        System.out.println(object.getClass().getSimpleName() + " saved as JSON");
    }
}
