package com.fdmgroup.cvgeneratorgradle.utils;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;



public class GeneratorConfig {
    static final String path = String.valueOf(Paths.get(".", File.separator , "config").normalize());

    public static void saveRecent(TreeMap<String, String> recentFiles, Set<String> names){
        File newFile = new File(path);
        if (!newFile.exists()) {
            System.out.println(newFile.mkdirs()+" created");
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path+File.separator+"recent.ser")))
        {

            oos.writeObject(recentFiles);
            System.out.println("TreeMap serialized successfully.");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try (ObjectOutputStream objectOutputStream =
                     new ObjectOutputStream(new FileOutputStream(path+File.separator+ "nameSet.set"))) {
            objectOutputStream.writeObject(names);
            System.out.println("Name set successfully serialized");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TreeMap<String, String> loadRecentFiles(){
        File newTemp = new  File(path);
        if (!newTemp.exists()) {
            System.out.println(newTemp.mkdirs()+" created");
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path+File.separator+ "recent.ser")))
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
                throw new RuntimeException(ex);
            }
            return new TreeMap<>();
        }
    }

    public static HashSet<? extends String> loadRecentFileNames(){
        File newTemp = new  File(path);
        if (!newTemp.exists()) {
            System.out.println(newTemp.mkdirs()+ " created");
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path +File.separator+ "nameSet.set")))
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
                throw new RuntimeException(ex);
            }
            return new HashSet<>();
        }
    }
}
