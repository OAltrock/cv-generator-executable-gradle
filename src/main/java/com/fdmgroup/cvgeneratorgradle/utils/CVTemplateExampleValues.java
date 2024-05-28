package com.fdmgroup.cvgeneratorgradle.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


import com.fdmgroup.cvgeneratorgradle.models.*;
import com.fdmgroup.cvgeneratorgradle.utils.SaveObjectToDocument;
import javafx.scene.control.Menu;


//this is for debugging/testing only
public class CVTemplateExampleValues {

    public static void main(String[] args) {

        CVTemplate cVExampleTemplate = createCVTemplateWithExampleValues();
        String userHome = System.getProperty("user.home");
        Path documentsPath = Paths.get(userHome, "Documents", "CVgenerator");

        // Create the "CVgenerator" directory if it doesn't exist
        if (!Files.exists(documentsPath)) {
            try {
                Files.createDirectories(documentsPath);
                System.out.println("CVgenerator directory created: " + documentsPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String outpathWord = documentsPath.resolve("CvAutoSave.docx").toString();
        String outpathPDF = documentsPath.resolve("CvAutoSave.PDF").toString();
        String outpathjson = documentsPath.resolve("CvAutoSave.json").toString();

        System.out.println("Word file path: " + outpathWord);
        System.out.println("PDF file path: " + outpathPDF);

        SaveObjectToJson.saveObjectAsJson(cVExampleTemplate, outpathjson, new Menu());
        try {
            SaveObjectToDocument.createDocument(cVExampleTemplate, "Word", outpathWord, new Menu());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            SaveObjectToDocument.createDocument(cVExampleTemplate, "pdf", outpathPDF, new Menu());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }

    public static CVTemplate createCVTemplateWithExampleValues() {
        // Create a new User object
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setRole("Consultant");

        // Create a new Location object
        Location location = new Location();
        location.setLocationName("New York");

        // Create a new Stream object
        Stream stream = new Stream();
        stream.setStreamName("Technical");

        // Create a list of example experiences
        List<Experience> experiences = new ArrayList<>();
        Experience experience = new Experience();
        experience.setJobTitle("Software Developer");
        experience.setStartDate("2018-01-01");
        experience.setEndDate("2022-12-31");
        experience.setCompanyName("google");
        List<String> positionFeatures = new ArrayList<>();
        positionFeatures.add("Team Leader");
        positionFeatures.add("React Developer");
        experience.setPositionFeatures(positionFeatures);
        experiences.add(experience);

        Experience experience2 = new Experience();
        experience2.setJobTitle("Software Developer");
        experience2.setStartDate("2016-01-01");
        experience2.setEndDate("2017-12-31");
        List<String> positionFeatures2 = new ArrayList<>();
        positionFeatures.add("Team Leader");
        positionFeatures.add("Java Developer");
        experience2.setPositionFeatures(positionFeatures2);
        experiences.add(experience2);

        // Create a list of example educations
        List<Education> educations = new ArrayList<>();
        Education education = new Education();
        education.setDegree("Bachelor of Science");
        education.setStudyTitle("Computer Science");
        education.setUniversityName("University of XYZ");
        education.setUniversityPlace("New York");
        education.setStartDate("2014-09-01");
        education.setEndDate("2018-06-30");
        education.setThesisTitle("Advanced Algorithms");
        List<String> keyModules = new ArrayList<>();
        keyModules.add("Module 1");
        keyModules.add("Module 2");
        education.setKeyModules(keyModules);
        educations.add(education);

        // Create a set of example competences
        HashSet<String> competences = new HashSet<>();
        competences.add("Java");
        competences.add("Python");

        // Create a set of example certificates
        HashSet<String> certificates = new HashSet<>();
        certificates.add("Oracle Certified Java Programmer");

        // Create a set of example languages
        HashSet<Language> languages = new HashSet<>();
        Language language = new Language();
        language.setLanguageType("English");
        languages.add(language);

        // Create a set of example interests
        HashSet<String> interests = new HashSet<>();
        interests.add("Reading");
        interests.add("Traveling");

        // Create a new CVTemplate object with the example values
        return new CVTemplate(user, "John", "Doe", location, stream, null, experiences, educations, competences, certificates, languages, interests);
    }
}
