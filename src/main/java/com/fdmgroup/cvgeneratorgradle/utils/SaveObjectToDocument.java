package com.fdmgroup.cvgeneratorgradle.utils;

import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.utils.FolderStructurePrinter;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.poi.xwpf.usermodel.*;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SaveObjectToDocument {


    public static void saveObjectAsWord(CVTemplate cvTemplate) throws IOException {

        Map<String, String> cVHashMap = HelperClass.convertCVObjectToHashMap(cvTemplate);

        //later: pass the output path when calling the function
        String documentsFolderPath = System.getProperty("user.home") + File.separator + "Documents"+ File.separator + "CVgenerator";
        String outputPath = documentsFolderPath + File.separator + "CvAutoSave.docx";
        String outputPathTestFile = documentsFolderPath + File.separator + "paragraphsFound.txt";
        String outputPathTestFile2 = documentsFolderPath + File.separator + "replacementsRecord.txt";

        // Create the output directory if it doesn't exist
        File outputDirectory = new File(documentsFolderPath);
        if (!outputDirectory.exists()) {
            boolean created = outputDirectory.mkdirs();
            if (!created) {
                throw new RuntimeException("Failed to create output directory: " + documentsFolderPath);
            }
        }

        System.out.println("saving word file under:");
        System.out.println(outputPath);

        //String wordTemplatePath = "resources/templates/fdm_cv_template_v1.docx";
        String wordTemplatePath =
                "C:/Users/Thomas/git/CV_Generator/cv-generator-executable-gradle/src/main/resources/com/fdmgroup/cvgeneratorgradle/templates/fdm_cv_template_test.docx";
        //String wordTemplatePath =
        //        "com/fdmgroup/cvgeneratorgradle/templates/fdm_cv_template_test.docx";

        // Try-with-resources block to handle streams
        //try (InputStream templateInputStream = SaveObjectToDocument.class.getClassLoader().getResourceAsStream(wordTemplatePath);
        //try (InputStream templateInputStream = getClass().getClassLoader()
        //        .getResourceAsStream(wordTemplatePath);
        try (FileInputStream templateInputStream = new FileInputStream(new File(wordTemplatePath));
             XWPFDocument document = new XWPFDocument(templateInputStream);
             OutputStream outputStream = new FileOutputStream(outputPath)) {

            // Record paragraphs to the test file
            //recordParagraphs(document, outputPathTestFile);

            System.out.println("==========content of the document is:==================");
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                System.out.println("=== " + paragraph.getText());
                replaceTextInParagraph(paragraph, cVHashMap);
            }
            System.out.println("============End of content=====================");


            // Replace placeholders in table cells (in case the document contains tables)
            System.out.println("==========content of the tables is:==================");
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            System.out.println("== " + paragraph.getText());
                            replaceTextInParagraph(paragraph, cVHashMap);
                        }
                    }
                }
            }
            System.out.println("============End of table content=====================");

            HelperClass.debugParagraphs(document);
            removeParagraphsWithPlaceholders(document);//remove remaining placeholders

            // Write the modified document to the output stream
            document.write(outputStream);
            System.out.println("word file saved");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // Helper method to replace placeholders in a paragraph with actual content
    private static void replaceTextInParagraph(XWPFParagraph paragraph, Map<String, String> replacements) {

        // Create a new run with the updated text
        String text = paragraph.getText();
        if (text.contains("{") && text.contains("}")) {
            boolean textChanged = false;
            //in case we have more than one value to change in the paragraph text, we take a substring with only the first value
            //String subText = text.substring(0, text.indexOf("}")+1);
            //String residualText = text.substring(text.indexOf("}") + 1);

            // Analyze the runs and determine the status list
            List<Integer> statusList = HelperClass.analyzeRuns(paragraph);

            // Rearrange the runs based on the status list
            List<Boolean> runContainsPlaceholder = HelperClass.rearrangeRuns(paragraph, statusList);

            // Check if the number of runs matches the size of runContainsPlaceholder
            if (paragraph.getRuns().size() != runContainsPlaceholder.size()) {
                throw new IllegalStateException("Mismatch between the number of runs and runContainsPlaceholder list.");
            }

            // Iterate over the runs and replace placeholders if necessary
            for (int i = 0; i < paragraph.getRuns().size(); i++) {
                if (runContainsPlaceholder.get(i)) {
                    XWPFRun run = paragraph.getRuns().get(i);
                    for (Map.Entry<String, String> entry : replacements.entrySet()) {
                        String key = entry.getKey();
                        if (run.getText(0).contains(key)) {
                            String replacementValue = entry.getValue();
                            if (run.getText(0).contains("{user.firstName}")) {
                                System.out.println("+++++ {user.firstName} found in run!!!!");
                                System.out.println("+++++ Replacement key is: " + key);
                                System.out.println("+++++ Replacement value is: " + replacementValue);
                            }
                            run.setText(run.getText(0).replace(key, replacementValue), 0);
                            if (key.equals("{user.firstName}")) {
                                System.out.println("+++++ new run text is: " + run.getText(0));
                            }
                        }
                    }
                }
            }
        }
    }

    private static void removeParagraphsWithPlaceholders(XWPFDocument document) {
        List<XWPFParagraph> paragraphsToRemove = new ArrayList<>();
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            String text = paragraph.getText();
            if (text.contains("{") && text.contains("}")){
                paragraphsToRemove.add(paragraph);
            }
        }

        // go through tables:
        List<XWPFParagraph> paragraphsToRemoveFromTable = new ArrayList<>();
        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        String text = paragraph.getText();
                        if (text.contains("{") && text.contains("}")) {
                            paragraphsToRemoveFromTable.add(paragraph);
                            System.out.println("~~ Table paragraph to remove: " + text );
                        }
                    }
                }
            }
        }

        for (XWPFParagraph paragraph : paragraphsToRemove) {
            System.out.println("~~~ Paragraphs that are removed: " + paragraph.getText() );
            document.removeBodyElement(document.getPosOfParagraph(paragraph));
        }

        //removing table paragraps does not work, but deleting the text does work.
        for (XWPFParagraph paragraph : paragraphsToRemoveFromTable) {
            System.out.println("~~~ Table Paragraphs that are removed: " + paragraph.getText() );
            for (XWPFRun run : paragraph.getRuns()) {
                System.out.println("old run text: " + run.getText(0));
                run.setText("", 0);
            }
        }

    }

    /*private static void removeUnusedParagraphs(XWPFDocument document, List<String> marks) {
        List<XWPFParagraph> paragraphsToRemove = new ArrayList<>();
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                String key = entry.getKey();
                if (paragraph.getText() != null && paragraph.getText().contains(key)) {
                    paragraphsToRemove.add(paragraph);
                    break; // Break once a placeholder is found in the paragraph
                }
            }
        }
        for (XWPFParagraph paragraph : paragraphsToRemove) {
            document.removeBodyElement(document.getPosOfParagraph(paragraph));
        }
    }*/
    private void replaceTextInParagraph_old(XWPFParagraph paragraph, Map<String, String> replacements) {
        String text = paragraph.getText();
        System.out.println("== " + text + " ==");
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            String key = entry.getKey();
            if (text.contains(key)) {
                text = text.replace(key, entry.getValue());
            }
        }
        //paragraph.setText(text);
    }

    private static void recordParagraphs(XWPFDocument document, String outputPathTestFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPathTestFile))) {
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                writer.write(paragraph.getText());
                writer.newLine();
            }
        }
    }
}
