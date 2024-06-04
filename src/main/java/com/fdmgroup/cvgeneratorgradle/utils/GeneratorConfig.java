package com.fdmgroup.cvgeneratorgradle.utils;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class GeneratorConfig {
    static final String path = System.getProperty("user.home")+"/cv-generator-config/config";

    public static void saveRecent(TreeMap<String, String> recentFiles, Set<String> names){
        File newFile = new File(path);
        if (!newFile.exists()) {
            newFile.mkdirs();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path+ "/recent.ser")))
        {

            oos.writeObject(recentFiles);
            System.out.println("TreeMap serialized successfully.");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try (ObjectOutputStream objectOutputStream =
                     new ObjectOutputStream(new FileOutputStream(path+ "/nameSet.set"))) {
            objectOutputStream.writeObject(names);
            System.out.println("Name set successfully serialized");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TreeMap<String, String> loadRecentFiles(){
        TreeMap<String, String> deserializedTreeMap = null;
        File newTemp = new  File(path);
        if (!newTemp.exists()) {
            newTemp.mkdirs();

        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path+ "/recent.ser")))
        {
            deserializedTreeMap = (TreeMap<String, String>) ois.readObject();
            if (deserializedTreeMap==null) deserializedTreeMap = new TreeMap<>();
            System.out.println("TreeMap deserialized successfully.");
        }
        catch (IOException | ClassNotFoundException e)
        {
            try(FileOutputStream fileOutputStream = new FileOutputStream(path + "/recent.ser")){
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return new TreeMap<>();
        }
        return deserializedTreeMap;
    }

    public static HashSet<String> loadRecentFileNames(){
        HashSet<String> deserializedTreeSet = null;
        File newTemp = new  File(path);
        if (!newTemp.exists()) {
                newTemp.mkdirs();

        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path + "/nameSet.set")))
        {
            deserializedTreeSet = (HashSet<String>) ois.readObject();
            if (deserializedTreeSet==null) deserializedTreeSet = new HashSet<>();
            System.out.println("Recent names deserialized successfully.");
        }
        catch (IOException | ClassNotFoundException e)
        {
            try(FileOutputStream fileOutputStream = new FileOutputStream(path + "/nameSet.set")){
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return new HashSet<>();
        }
        return deserializedTreeSet;
    }
}
