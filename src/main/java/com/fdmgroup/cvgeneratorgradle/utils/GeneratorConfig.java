package com.fdmgroup.cvgeneratorgradle.utils;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;



public class GeneratorConfig {
    static final String path = System.getProperty("user.home") + "/cv-generator-config/config";

    public static void saveRecent(TreeMap<String, String> recentFiles, Set<String> names, BorderPane main) {
        File newFile = new File(path);
        if (!newFile.exists()) {
            if(newFile.mkdirs()) System.out.println(newFile+" created");
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path + "/recent.ser"))) {

            oos.writeObject(recentFiles);
            System.out.println("TreeMap serialized successfully.");
        } catch (IOException e) {
            Label errLabel = new Label("Error:\n" +
                    e.getMessage());
            errLabel.setAlignment(Pos.CENTER);
            errLabel.setWrapText(true);
            HBox newHBox = new HBox(errLabel);
            main.setCenter(newHBox);
        }
        try (ObjectOutputStream objectOutputStream =
                     new ObjectOutputStream(new FileOutputStream(path + "/nameSet.set"))) {
            objectOutputStream.writeObject(names);
            System.out.println("Name set successfully serialized");
        } catch (IOException e) {
            Label errLabel = new Label("Error:\n"+
                    e.getMessage());
            errLabel.setAlignment(Pos.CENTER);
            errLabel.setWrapText(true);
            HBox newHBox = new HBox(errLabel);
            main.setCenter(newHBox);
        }
    }

    public static TreeMap<String, String> loadRecentFiles(BorderPane main){
        File newTemp = new  File(path);
        if (!newTemp.exists()) {
            if(newTemp.mkdirs()) System.out.println(newTemp+" created");
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path+"/recent.ser")))
        {
            TreeMap<String,String> deserializedTreeMap = new TreeMap<>();
            Object obj = ois.readObject();
            if (obj==null) return new TreeMap<>();
            if (obj instanceof TreeMap<?,?>) ((TreeMap<?, ?>) obj).forEach((key,value) -> {
                if (key instanceof String && value instanceof String) deserializedTreeMap.put((String) key, (String) value);
            });
            System.out.println("TreeMap deserialized successfully.");
            return deserializedTreeMap;
        }
        catch (IOException | ClassNotFoundException e)
        {
            try(FileOutputStream fileOutputStream = new FileOutputStream(path +File.separator+ "recent.ser")){
                fileOutputStream.flush();
                System.out.println("New config file created");
            } catch (IOException ex) {
                Label errLabel = new Label("Error:\n"+
                        ex.getMessage());
                errLabel.setAlignment(Pos.CENTER);
                errLabel.setWrapText(true);
                HBox newHBox = new HBox(errLabel);
                main.setCenter(newHBox);
            }
            return new TreeMap<>();
        }
    }

    public static HashSet<? extends String> loadRecentFileNames(BorderPane main){
        File newTemp = new  File(path);
        if (!newTemp.exists()) {
            if(newTemp.mkdirs()) System.out.println(newTemp+" created");
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path +"/nameSet.set")))
        {
            Object obj = ois.readObject();
            HashSet<String> res = new HashSet<>();
            if (obj==null) return new HashSet<>();

            if (obj instanceof HashSet) ((HashSet<?>) obj).forEach(item -> {
                if (item instanceof String) res.add((String) item);
            });
            System.out.println("Recent names deserialized successfully.");
            return res;
        }
        catch (IOException | ClassNotFoundException e)
        {
            try(FileOutputStream fileOutputStream = new FileOutputStream(path +File.separator+ "nameSet.set")){
                fileOutputStream.flush();
                System.out.println("New config file created");
            } catch (IOException ex) {
                Label errLabel = new Label("Error:\n"+
                        ex.getMessage());
                errLabel.setAlignment(Pos.CENTER);
                errLabel.setWrapText(true);
                HBox newHBox = new HBox(errLabel);
                main.setCenter(newHBox);
            }
            return new HashSet<>();
        }
    }
}
