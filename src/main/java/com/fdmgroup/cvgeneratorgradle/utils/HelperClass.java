package com.fdmgroup.cvgeneratorgradle.utils;

import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.Education;
import com.fdmgroup.cvgeneratorgradle.models.Experience;
import com.fdmgroup.cvgeneratorgradle.models.Language;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class HelperClass {

//Would be nice to have, but not working properly yet (not used). We use the "CV Object" version instead
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

    public static Map<String, String> convertCVObjectToHashMap(CVTemplate obj) {
        Map<String, String> hashMap = new HashMap<>();

        // Extract user fields
        hashMap.put("{user.firstName}", obj.getUser().getFirstName());
        hashMap.put("{user.lastName}", obj.getUser().getLastName());
        hashMap.put("{user.email}", obj.getUser().getEmail());
        hashMap.put("{user.role}", obj.getUser().getRole());

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

//this method searches a paragraph for a string and is returning the index of the first run, that contains this string.
    public static int findRunContainingText(XWPFParagraph paragraph, String searchString) {
        List<XWPFRun> runs = paragraph.getRuns();
        for (int i = 0; i < runs.size(); i++) {
            XWPFRun run = runs.get(i);
            if (run.getText(0) != null && run.getText(0).contains(searchString)) {
                System.out.println("Text of run with " + searchString  + "is:");
                System.out.println(run.getText(0));
                return i; // Return the index of the run containing the search string
            }
        }
        // If the search string is not found in any run, return -1
        return -1;
    }

    public static List<Integer> analyzeRuns(XWPFParagraph paragraph) {
        List<XWPFRun> runs = paragraph.getRuns();
        List<Integer> statusList = new ArrayList<>();

        for (XWPFRun run : runs) {

            //create a list of bracket order for analysis:
            List<Integer> bracketList = new ArrayList<>();//add 1 for "{" and 2 for "}"
            String text = run.getText(0);
            if (text != null && !text.isEmpty()) {
                for (char c : text.toCharArray()) {
                    if (c == '{') {
                        bracketList.add(1);
                    } else if (c == '}') {
                        bracketList.add(2);
                    }
                }
            }

            // Analyze the bracket list
            int size = bracketList.size();
            if (bracketList.isEmpty()) {
                statusList.add(0);
            }
            else if (size == 1) {
                if (bracketList.get(0) == 1) {
                    statusList.add(1);
                } else if (bracketList.get(0) == 2) {
                    statusList.add(2);
                }  }
            else if (bracketList.get(0) == 1 && bracketList.get(size - 1) == 2) {
                statusList.add(0);
            } else if (bracketList.get(0) == 1) {//so the last entry must be 1 i this is reached
                statusList.add(1);
            } else if (bracketList.get(0) == 2 && bracketList.get(size - 1) == 1) {
                statusList.add(3);
            } else if (bracketList.get(0) == 2) {
                statusList.add(2);
            }
        }

        return statusList;
    }

    public static List<Boolean> rearrangeRuns(XWPFParagraph paragraph, List<Integer> statusList) {
        List<XWPFRun> runs = paragraph.getRuns();
        List<Boolean> placeholderExistsList = new ArrayList<>();
        int index = 0;

        while (index < statusList.size()) {
            int status = statusList.get(index);

            // Case 0: No changes needed
            if (status == 0) {
                placeholderExistsList.add(false); // No placeholder in this run
                index++;
                continue;
            }
            else if (status == 2) {
                throw new IllegalStateException("Invalid bracket sequence: closing bracket without opening one.");
            }
            boolean continueMerge = true;

            // Case 1 or 3: Merge with next runs until encountering status 2
            StringBuilder mergedText = new StringBuilder(runs.get(index).getText(0));
            int nextIndex = index + 1;

            while (continueMerge && nextIndex < statusList.size()) {
                mergedText.append(runs.get(nextIndex).getText(0));
                int nextStatus = statusList.get(nextIndex);
                paragraph.removeRun(nextIndex); // Remove the merged runs
                statusList.remove(nextIndex); // Remove the corresponding status
                if (nextStatus == 2) {//status 2, then we have "}" as last bracket in this run, so we can stop merging.
                    continueMerge = false;
                }
            }

            // Check if the text ends with an opening bracket
            if (continueMerge) {
                throw new IllegalStateException("Invalid bracket sequence: string ends with opening bracket without a closing one.");
            }

            // Update the text of the current run
            runs.get(index).setText(mergedText.toString(), 0);
            placeholderExistsList.add(true); // Placeholder exists

            // Move to the next run after the merged section
            index = nextIndex;
        }
        return placeholderExistsList;
    }
    public static void debugParagraphs(XWPFDocument document) {
        System.out.println("======= Debugging Paragraphs =======");
        int paragraphCount = 0;

        // Iterate through all paragraphs in the document
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            // Check if the paragraph contains "{" and "}"
            String paragraphText = paragraph.getText();
            if (paragraphText.contains("{") && paragraphText.contains("}")) {
                paragraphCount++;
                System.out.println("Paragraph " + paragraphCount + ": " + paragraphText);

                // Print the text of each run within the paragraph
                List<XWPFRun> runs = paragraph.getRuns();
                for (int i = 0; i < runs.size(); i++) {
                    System.out.println("  Run " + (i + 1) + ": " + runs.get(i).getText(0));
                }
            }
        }

        // Output the total number of paragraphs with placeholders
        System.out.println("Total paragraphs with placeholders: " + paragraphCount);
    }

}
