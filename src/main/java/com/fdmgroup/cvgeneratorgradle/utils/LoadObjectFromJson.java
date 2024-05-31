package com.fdmgroup.cvgeneratorgradle.utils;

import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class LoadObjectFromJson {

    /**
     * Creates a {@link CVTemplate} from given file path
     * @param filePath of file that ought to be converted to CVTemplate class
     * @return a CVTemplate class instance
     * @throws FileNotFoundException
     */
    public static CVTemplate loadObjectFromJson (String filePath) throws FileNotFoundException {
        Gson gson = new Gson();
       return gson.fromJson(new FileReader(filePath), CVTemplate.class);
    }
}
