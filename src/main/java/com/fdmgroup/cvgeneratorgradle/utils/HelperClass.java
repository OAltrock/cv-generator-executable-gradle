package com.fdmgroup.cvgeneratorgradle.utils;

import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.Education;
import com.fdmgroup.cvgeneratorgradle.models.Experience;
import com.fdmgroup.cvgeneratorgradle.models.Language;
import com.fdmgroup.cvgeneratorgradle.controller.PersonalInformationController;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
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
     * and mapping them in the HashMap. The Hashmap keys are defined placeholders used in the docx template,
     * the values are variable values taken from the object.
     *
     * @param obj The CVTemplate object to be converted
     * @return A HashMap containing placeholders as keys and their corresponding values from the CVTemplate object
     */
    public static Map<String, String> convertCVObjectToHashMap(CVTemplate obj) {
        Map<String, String> hashMap = new HashMap<>();

        // Extract user fields
        if (obj.getUser().getFirstName() != null && !obj.getUser().getFirstName().isEmpty()) {
            hashMap.put("{user.firstName}", obj.getUser().getFirstName());
        }
        if (obj.getUser().getLastName() != null && !obj.getUser().getLastName().isEmpty()) {
            hashMap.put("{user.lastName}", obj.getUser().getLastName());
        }
        if (obj.getUser().getEmail() != null && !obj.getUser().getEmail().isEmpty()) {
            hashMap.put("{user.email}", obj.getUser().getEmail());
        }
        if (obj.getUser().getRole() != null && !obj.getUser().getRole().isEmpty()) {
            hashMap.put("{user.role}", obj.getUser().getRole());
        }
        if (obj.getProfile() != null && !obj.getProfile().isEmpty()) {
            hashMap.put("{profile}", obj.getProfile());
        }

        // Extract location fields
        if (obj.getLocation() != null) {
            if (obj.getLocation().getLocationName() != null && !obj.getLocation().getLocationName().isEmpty()) {
                hashMap.put("{location.locationName}", obj.getLocation().getLocationName());
            }
        }

        // Extract stream fields
        if (obj.getStream() != null) {
            if (obj.getStream().getStreamName() != null && !obj.getStream().getStreamName().isEmpty()) {
                hashMap.put("{stream.streamName}", obj.getStream().getStreamName());
            }
        }

        //Extract Stream Modules from HashSet in PersonalInfoControler:
        {
            HashSet<String> streamSkillSet = new HashSet<>();
            if (obj.getStream().getStreamName().equalsIgnoreCase("technical")) {
                streamSkillSet = PersonalInformationController.TECHNICAL;
            } else if (obj.getStream().getStreamName().equalsIgnoreCase("business")) {
                streamSkillSet = PersonalInformationController.BUSINESS;
            }
            int i = 0;
            for (String skill : streamSkillSet) {
                hashMap.put("{stream.Skills[" + i + "]}", skill);
                i++;
            }
        }


        // Extract experiences
        if (obj.getExperiences() != null) {
            for (int i = 0; i < obj.getExperiences().size(); i++) {
                Experience exp = obj.getExperiences().get(i);
                if (exp.getJobTitle() != null && !exp.getJobTitle().isEmpty()) {
                    hashMap.put("{experiences[" + i + "].jobTitle}", exp.getJobTitle());
                }
                if (exp.getStartDate() != null && !exp.getStartDate().isEmpty()) {
                    hashMap.put("{experiences[" + i + "].startDate}", exp.getStartDate());
                }
                if (exp.getEndDate() != null && !exp.getEndDate().isEmpty()) {
                    hashMap.put("{experiences[" + i + "].endDate}", exp.getEndDate());
                }
                if (exp.getCompanyName() != null && !exp.getCompanyName().isEmpty()) {
                    hashMap.put("{experiences[" + i + "].companyName}", exp.getCompanyName());
                }
                if (exp.getDescription() != null && !exp.getDescription().isEmpty()) {
                    hashMap.put("{experiences[" + i + "].description}", exp.getDescription());
                }

                // Extract positionFeatures
                if (exp.getPositionFeatures() != null) {
                    for (int j = 0; j < exp.getPositionFeatures().size(); j++) {
                        String positionFeature = exp.getPositionFeatures().get(j);
                        if (positionFeature != null && !positionFeature.isEmpty()) {
                            hashMap.put("{experiences[" + i + "].positionFeature[" + j + "]}", positionFeature);
                        }
                    }
                }
            }
        }

        // Extract educations
        if (obj.getEducations() != null) {
            for (int i = 0; i < obj.getEducations().size(); i++) {
                Education edu = obj.getEducations().get(i);
                if (edu.getDegree() != null && !edu.getDegree().isEmpty()) {
                    hashMap.put("{educations[" + i + "].degree}", edu.getDegree());
                }
                if (edu.getStudyTitle() != null && !edu.getStudyTitle().isEmpty()) {
                    hashMap.put("{educations[" + i + "].studyTitle}", edu.getStudyTitle());
                }
                if (edu.getUniversityName() != null && !edu.getUniversityName().isEmpty()) {
                    hashMap.put("{educations[" + i + "].universityName}", edu.getUniversityName());
                }
                if (edu.getUniversityPlace() != null && !edu.getUniversityPlace().isEmpty()) {
                    hashMap.put("{educations[" + i + "].universityPlace}", edu.getUniversityPlace());
                }
                if (edu.getStartDate() != null && !edu.getStartDate().isEmpty()) {
                    hashMap.put("{educations[" + i + "].startDate}", edu.getStartDate());
                }
                if (edu.getEndDate() != null && !edu.getEndDate().isEmpty()) {
                    hashMap.put("{educations[" + i + "].endDate}", edu.getEndDate());
                }
                if (edu.getThesisTitle() != null && !edu.getThesisTitle().isEmpty()) {
                    hashMap.put("{educations[" + i + "].thesisTitle}", edu.getThesisTitle());
                }

                // Extract keyModules
                if (edu.getKeyModules() != null) {
                    for (int j = 0; j < edu.getKeyModules().size(); j++) {
                        String keyModule = edu.getKeyModules().get(j);
                        if (keyModule != null && !keyModule.isEmpty()) {
                            hashMap.put("{educations[" + i + "].keyModules[" + j + "]}", keyModule);
                        }
                    }
                }
            }
        }

        // Extract competences
        if (obj.getFdmSkills() != null) {
            int index = 0;
            for (String competence : obj.getFdmSkills()) {
                if (competence != null && !competence.isEmpty()) {
                    hashMap.put("{competences[" + index++ + "]}", competence);
                }
            }
        }

        // Extract certificates
        if (obj.getCertificates() != null) {
            int index = 0;
            for (String certificate : obj.getCertificates()) {
                if (certificate != null && !certificate.isEmpty()) {
                    hashMap.put("{certificates[" + index++ + "]}", certificate);
                }
            }
        }

        // Extract languages
        if (obj.getLanguages() != null) {
            int index = 0;
            for (Language lang : obj.getLanguages()) {
                if (lang.getLanguageType() != null && !lang.getLanguageType().isEmpty()) {
                    hashMap.put("{languages[" + index + "].languageType}", lang.getLanguageType());
                }
                if (lang.getLanguageLevel() != null) {
                    hashMap.put("{languages[" + index + "].languageLevel}", lang.getLanguageLevel().toString());
                }

                index++;
            }
        }

        // Extract interests
        if (obj.getInterests() != null) {
            int index = 0;
            for (String interest : obj.getInterests()) {
                if (interest != null && !interest.isEmpty()) {
                    hashMap.put("{interests[" + index++ + "]}", interest);
                }
            }
        }

        return hashMap;
    }

    public static void printHashMap(Map<String, String> hashMap) {
        for (String key : hashMap.keySet()) {
            System.out.println(key + ": " + hashMap.get(key));
        }
        System.out.println();
    }


}
