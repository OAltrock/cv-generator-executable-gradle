package com.fdmgroup.cvgeneratorgradle.utils;

import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.Education;
import com.fdmgroup.cvgeneratorgradle.models.Experience;
import com.fdmgroup.cvgeneratorgradle.models.Language;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
public class HelperClass {

    /**
     * Converts any object to a HashMap by iterating over its fields and adding the field names and values
     * to the HashMap. Would be nice to have, but not working properly yet (not used). We use the "CV Object" version instead
     *
     *
     * @param obj The object to be converted to a HashMap
     * @return A HashMap containing the object's field names as keys and their corresponding values
     */
    public static Map<String, String> convertAnyObjectToHashMap(Object obj) {
        Map<String, String> hashMap = new HashMap<>();
        Class<?> clazz = obj.getClass();

        // Iterate through all fields of the object
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true); // Enable access to private field
            try {
                // Get the field value and put it into the HashMap
                Object value = field.get(obj);
                hashMap.put(field.getName(), String.valueOf(value));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return hashMap;
    }

    /**
     * Converts a CVTemplate object to a HashMap by extracting selected fields from the object
     * and mapping them in the HashMap. The Hashmap keys are defined placeholders unsed in the docx template,
     * the values are variable values taken from the object.
     *
     * @param obj The CVTemplate object to be converted
     * @return A HashMap containing placeholders as keys and their corresponding values from the CVTemplate object
     */
    public static Map<String, String> convertCVObjectToHashMap(CVTemplate obj) {
        Map<String, String> hashMap = new HashMap<>();

        // Extract user fields
        hashMap.put("{user.firstName}", obj.getUser().getFirstName());
        hashMap.put("{user.lastName}", obj.getUser().getLastName());
        hashMap.put("{user.email}", obj.getUser().getEmail());
        hashMap.put("{user.role}", obj.getUser().getRole());

        hashMap.put("{profile}", obj.getProfile());

        // Extract location fields
        hashMap.put("{location.locationName}", obj.getLocation().getLocationName());
        //hashMap.put("{location.minExperience}", String.valueOf(obj.getLocation().getMinExperience()));
        //hashMap.put("{location.maxExperience}", String.valueOf(obj.getLocation().getMaxExperience()));

        // Extract stream fields
        hashMap.put("{stream.streamName}", obj.getStream().getStreamName());

        // Extract experiences
        for (int i = 0; i < obj.getExperiences().size(); i++) {
            Experience exp = obj.getExperiences().get(i);
            hashMap.put("{experiences[" + i + "].jobTitle}", exp.getJobTitle());
            hashMap.put("{experiences[" + i + "].startDate}", exp.getStartDate());
            hashMap.put("{experiences[" + i + "].endDate}", exp.getEndDate());
            hashMap.put("{experiences[" + i + "].companyName}", exp.getCompanyName());
            //hashMap.put("{experiences[" + i + "].description}", exp.getDescription());

            // Extract positionFeatures
            for (int j = 0; j < exp.getPositionFeatures().size(); j++) {
                hashMap.put("{experiences[" + i + "].positionFeature[" + j + "]}", exp.getPositionFeatures().get(j));
            }
        }

        // Extract educations
        for (int i = 0; i < obj.getEducations().size(); i++) {
            Education edu = obj.getEducations().get(i);
            hashMap.put("{educations[" + i + "].degree}", edu.getDegree());
            hashMap.put("{educations[" + i + "].studyTitle}", edu.getStudyTitle());
            hashMap.put("{educations[" + i + "].universityName}", edu.getUniversityName());
            hashMap.put("{educations[" + i + "].universityPlace}", edu.getUniversityPlace());
            hashMap.put("{educations[" + i + "].startDate}", edu.getStartDate());
            hashMap.put("{educations[" + i + "].endDate}", edu.getEndDate());
            hashMap.put("{educations[" + i + "].thesisTitle}", edu.getThesisTitle());

            // Extract keyModules
            for (int j = 0; j < edu.getKeyModules().size(); j++) {
                hashMap.put("{educations[" + i + "].keyModules[" + j + "]}", edu.getKeyModules().get(j));
            }
        }

        // Extract competences
        int index = 0;
        for (String competence : obj.getCompetences()) {
            hashMap.put("{competences[" + index++ + "]}", competence);
        }

        // Extract certificates
        index = 0;
        for (String certificate : obj.getCertificates()) {
            hashMap.put("{certificates[" + index++ + "]}", certificate);
        }

        // Extract languages
        index = 0;
        for (Language lang : obj.getLanguages()) {
            hashMap.put("{languages[" + index + "].languageType}", lang.getLanguageType());
            // Add other language fields similarly
            index++;
        }

        // Extract interests
        index = 0;
        for (String interest : obj.getInterests()) {
            hashMap.put("{interests[" + index++ + "]}", interest);
        }

        return hashMap;
    }


}
