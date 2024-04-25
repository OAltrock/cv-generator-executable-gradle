package com.fdmgroup.cvgeneratorgradle.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SaveObjectToJson {
    public static void saveObjectAsJson(Object object) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(object);

        // create file path with user and his local documents folder e.g. C:\Users\Username\Documents
        String documentsFolderPath = System.getProperty("user.home") + File.separator + "Documents";
        String filePath = documentsFolderPath + File.separator + "cvTemplate.json";
        System.out.println(filePath);

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(json);
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Speichern des Objekts als JSON: " + e.getMessage());
        }

        System.out.println(object.getClass().getSimpleName() + " wurde als JSON gespeichert.");
    }
}
