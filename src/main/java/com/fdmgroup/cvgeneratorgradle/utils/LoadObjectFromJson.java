package com.fdmgroup.cvgeneratorgradle.utils;

import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class LoadObjectFromJson {

    public static CVTemplate loadObjectFromJson (String filePath) throws FileNotFoundException {
        Gson gson = new Gson();
        //String filePath = System.getProperty("user.home")+"/documents/completeSave.json";
        //System.out.println(filePath);
        return gson.fromJson(new FileReader(filePath), CVTemplate.class);
    }
}
