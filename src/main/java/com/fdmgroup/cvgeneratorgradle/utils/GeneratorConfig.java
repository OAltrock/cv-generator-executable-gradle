package com.fdmgroup.cvgeneratorgradle.utils;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class GeneratorConfig {

    public static void saveRecent(TreeMap<String, String> recentFiles, Set<String> names){
        File newFile = new File("./config");
        if (!newFile.exists()) {
            newFile.mkdirs();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./config/recent.ser")))
        {

            oos.writeObject(recentFiles);
            System.out.println("TreeMap serialized successfully.");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try (ObjectOutputStream objectOutputStream =
                     new ObjectOutputStream(new FileOutputStream("./config/nameSet.set"))) {
            objectOutputStream.writeObject(names);
            System.out.println("Name set successfully serialized");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TreeMap<String, String> loadRecentFiles(){
        TreeMap<String, String> deserializedTreeMap = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./config/recent.ser")))
        {
            deserializedTreeMap = (TreeMap<String, String>) ois.readObject();
            System.out.println("TreeMap deserialized successfully.");
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return deserializedTreeMap;
    }

    public static HashSet<String> loadRecentFileNames(){
        HashSet<String> deserializedTreeSet = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./config/nameSet.set")))
        {
            deserializedTreeSet = (HashSet<String>) ois.readObject();
            System.out.println("Recent names deserialized successfully.");
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return deserializedTreeSet;
    }
}
