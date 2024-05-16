package com.fdmgroup.cvgeneratorgradle.utils;

import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.utils.FolderStructurePrinter;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.poi.xwpf.usermodel.*;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SaveObjectToDocument {

    /**
     * Creates a Word or PDF document from the provided CVTemplate object and saves it to the specified output path.
     * Additionally, it creates an auto-save file in JSON format for the CVTemplate object.
     *
     * <p>This method is typically called from the summary page and controls the document creation process.
     * It first creates an auto-save file in JSON format containing the CVTemplate object data. Then, based
     * on the specified format (either "Word" or "PDF"), it calls the appropriate method to generate the
     * corresponding document and saves it to the provided output path.</p>
     *
     * @param cvTemplate The CVTemplate object containing the data to be populated in the document.
     * @param format     The desired document format, either "Word" or "PDF" (case-insensitive).
     * @param outputPath The path where the generated document should be saved.
     * @throws IOException If an I/O error occurs while creating the auto-save file or generating the document.
     * @throws IllegalArgumentException If an unsupported document format is provided.
     */
    public static void createDocument(CVTemplate cvTemplate, String format, String outputPath) throws IOException {

        //creating an auto save in json format:
        String saveFilePath = ".\\saves\\autosave_fullCV.json";
        SaveObjectToJson.saveObjectAsJson(cvTemplate, saveFilePath);

        if ("docx".equalsIgnoreCase(format) || "word".equalsIgnoreCase(format)) {
            saveObjectAsWord(cvTemplate, outputPath);
        } else if ("PDF".equalsIgnoreCase(format)) {
            saveObjectAsPDF(cvTemplate, outputPath);
        } else {
            throw new IllegalArgumentException("Unsupported document format: " + format);
        }
    }

//save word to auto chosen file in home directory:
    public static void saveObjectAsWord(CVTemplate cvTemplate) throws IOException {
        String documentsFolderPath = System.getProperty("user.home") + File.separator + "Documents"+ File.separator + "CVgenerator";
        String outputPath = documentsFolderPath + File.separator + "CvAutoSave.docx";
        saveObjectAsWord(cvTemplate, outputPath);
    }

    /**
     * Generates a Word document from a given CVTemplate object and saves it to the specified output path.
     *
     * @param cvTemplate The CVTemplate object containing the data to be populated in the Word document.
     * @param outputPath The path where the generated Word document should be saved.
     * @throws IOException If an I/O error occurs while reading the template or writing the output file.
     * @author Thomas Elble thomas.elble@fdmgroup.com
     * @version 1.0 (2024-05-15)
     */
    public static void saveObjectAsWord(CVTemplate cvTemplate, String outputPath) throws IOException {

        Map<String, String> cVHashMap = HelperClass.convertCVObjectToHashMap(cvTemplate);
        String documentsFolderPath = System.getProperty("user.home") + File.separator + "Documents"+ File.separator + "CVgenerator";
        //String outputPath = documentsFolderPath + File.separator + "CvAutoSave.docx";
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

        //System.out.println("saving word file under:");
        //System.out.println(outputPath);

        //String wordTemplatePath = "./src/main/resources/templates/fdm_cv_template_v1.docx";
        String wordTemplatePath =
                "./src/main/resources/com/fdmgroup/cvgeneratorgradle/templates/fdm_cv_template_test.docx";

        try (FileInputStream templateInputStream = new FileInputStream(new File(wordTemplatePath));
             XWPFDocument document = new XWPFDocument(templateInputStream);
             OutputStream outputStream = new FileOutputStream(outputPath)) {

            //System.out.println("==========content of the document is:==================");
            for (XWPFParagraph paragraph : document.getParagraphs()) {
            //    System.out.println("=== " + paragraph.getText());
                replaceTextInParagraph(paragraph, cVHashMap);
            }
            //System.out.println("============End of content=====================");

            // Replace placeholders in table cells (in case the document contains tables)
            //System.out.println("==========content of the tables is:==================");
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
            //                System.out.println("== " + paragraph.getText());
                            replaceTextInParagraph(paragraph, cVHashMap);
                        }
                    }
                }
            }
            //System.out.println("============End of table content=====================");

            //HelperClass.debugParagraphs(document);
            removeParagraphsWithPlaceholders(document);//remove remaining placeholders

            // Write the modified document to the output stream
            document.write(outputStream);
            System.out.println("word file saved");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Helper method to replace placeholders in a paragraph with user data.
     *
     * <p>This method iterates over the runs (text segments) within a paragraph and replaces any
     * placeholders (text enclosed in curly braces '{...}') with the corresponding replacement values
     * from the provided map.</p>
     *
     * <p>The method performs the following steps:
     * <ol>
     *     <li>Analyzes the runs in the paragraph to determine if they contain placeholders or not.</li>
     *     <li>Rearranges the runs based on the analysis, grouping runs with and without placeholders.</li>
     *     <li>Iterates over the rearranged runs, and for each run containing placeholders, replaces the placeholders with the corresponding replacement values from the provided map.</li>
     * </ol>
     * </p>
     *
     * @param paragraph     The XWPFParagraph object representing the paragraph to process.
     * @param replacements  A map containing the placeholders as keys and their replacement values as values.
     * @throws IllegalStateException If there is a mismatch between the number of runs and the size of the runContainsPlaceholder list.
     * @author Thomas Elble thomas.elble@fdmgroup.com
     * @version 1.0 (2024-05-15)
     */
    private static void replaceTextInParagraph(XWPFParagraph paragraph, Map<String, String> replacements) {

        // Create a new run with the updated text
        String text = paragraph.getText();
        if (text.contains("{") && text.contains("}")) {
            boolean textChanged = false;

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
                            run.setText(run.getText(0).replace(key, replacementValue), 0);
                        }
                    }
                }
            }
        }
    }

    /**
     * Generates a PDF document from a given CVTemplate object and saves it to the specified output path.
     *
     * <p>This method first generates a temporary Word document using the {@link #saveObjectAsWord(CVTemplate, String)}
     * method, and then converts the Word document to a PDF file using the Apache POI XWPF Converter library.</p>
     *
     * <p>The steps involved in the conversion process are:
     * <ol>
     *     <li>Create a temporary Word document from the provided CVTemplate object.</li>
     *     <li>Open the temporary Word document using an XWPFDocument instance.</li>
     *     <li>Convert the XWPFDocument to a PDF file using the PdfConverter utility class.</li>
     *     <li>Write the PDF file to the specified output path.</li>
     *     <li>Delete the temporary Word document file.</li>
     * </ol>
     * </p>
     *
     * @param cvTemplate The CVTemplate object containing the data to be populated in the PDF document.
     * @param outputPath The path where the generated PDF document should be saved.
     * @throws IOException If an I/O error occurs while reading/writing files or during the conversion process.
     */
    public static void saveObjectAsPDF(CVTemplate cvTemplate, String outputPath) throws IOException {
        String wordTempPath = System.getProperty("java.io.tmpdir") + "tempCvDocument.docx";
        saveObjectAsWord(cvTemplate, wordTempPath);

        try (FileInputStream wordInputStream = new FileInputStream(new File(wordTempPath));
             XWPFDocument document = new XWPFDocument(wordInputStream);
             OutputStream pdfOutputStream = new FileOutputStream(outputPath)) {

            PdfOptions options = PdfOptions.create();
            PdfConverter.getInstance().convert(document, pdfOutputStream, options);

            System.out.println("PDF file saved");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            new File(wordTempPath).delete();
        }
    }

    /**
     * Removes paragraphs and table cells that contain placeholders from the given XWPFDocument.
     *
     * <p>This method iterates over all paragraphs in the document body and table cells, and identifies
     * those that contain placeholders (text enclosed in curly braces '{...}'). It then removes these
     * paragraphs from the document body and clears the text content of the table cell paragraphs.</p>
     *
     * <p>Note that removing paragraphs from table cells directly is not possible due to a limitation
     * in the Apache POI library. Therefore, this method clears the text content of table cell paragraphs
     * containing placeholders instead of removing them.</p>
     *
     * @param document The XWPFDocument from which to remove paragraphs with placeholders.
     */
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

        //removing table paragraphs does not work, but deleting the text does work.
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

}
