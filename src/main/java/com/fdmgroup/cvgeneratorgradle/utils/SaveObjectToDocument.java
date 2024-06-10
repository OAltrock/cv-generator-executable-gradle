package com.fdmgroup.cvgeneratorgradle.utils;

import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.utils.FolderStructurePrinter;

import com.sun.javafx.binding.BindingHelperObserver;

import javafx.scene.control.Menu;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.poi.xwpf.usermodel.*;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import picocli.CommandLine;

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
     * @param recent     The Menu object representing recent files or actions (if applicable).
     * @throws IOException If an I/O error occurs while creating the auto-save file or generating the document.
     * @throws IllegalArgumentException If an unsupported document format is provided.
     */
    public static void createDocument(CVTemplate cvTemplate, String format, String outputPath, Menu recent) throws IOException {

        //creating an auto save in json format:
        String saveFilePath = SaveObjectToJson.savePath+File.separator+"autosave_fullCV.json";
        SaveObjectToJson.saveObjectAsJson(cvTemplate, saveFilePath);

        if ("docx".equalsIgnoreCase(format) || "word".equalsIgnoreCase(format)) {
            saveObjectAsWord(cvTemplate, outputPath, false);
        } else if ("PDF".equalsIgnoreCase(format)) {
            saveObjectAsPDF(cvTemplate, outputPath);
        } else {
            throw new IllegalArgumentException("Unsupported document format: " + format);
        }
    }

    /**
     * Overloaded method to create a document without a Menu object.
     *
     * @param cvTemplate The CVTemplate object containing the data to be populated in the document.
     * @param format     The desired document format, either "Word" or "PDF" (case-insensitive).
     * @param outputPath The path where the generated document should be saved.
     * @throws IOException If an I/O error occurs while creating the auto-save file or generating the document.
     * @throws IllegalArgumentException If an unsupported document format is provided.
     */
    public static void createDocument(CVTemplate cvTemplate, String format, String outputPath) throws IOException {
        createDocument(cvTemplate, format, outputPath, new Menu());
    }


    public static void saveObjectAsWord(CVTemplate cvTemplate) throws IOException {
        String documentsFolderPath = System.getProperty("user.home") + File.separator + "Documents"+ File.separator + "CVgenerator";
        String outputPath = documentsFolderPath + File.separator + "CvAutoSave.docx";
        saveObjectAsWord(cvTemplate, outputPath);
    }

    /**
     * Overloaded method call to save the CVTemplate object as a Word document to the specified output path.
     *
     * @param cvTemplate The CVTemplate object containing the data to be populated in the Word document.
     * @param outputPath The path where the generated Word document should be saved.
     * @throws IOException If an I/O error occurs while saving the document.
     */
    public static void saveObjectAsWord(CVTemplate cvTemplate, String outputPath) throws IOException {
        saveObjectAsWord(cvTemplate, outputPath, false);
    }

    /**
     * Generates a Word document from a given CVTemplate object and saves it to the specified output path.
     * if {@param createPdfLater} is set to true, the table formatting is changed to improve table structure in the pdf document
     * (word by default uses automatic settings for e.g. table cell heights, which can not be read by the PDF libaries,
     * therefore, we have to set cell heights to an estimated fixed value).
     * if you don't want to use the  word file to create a pdf in a second step, set {@param createPdfLater} to false
     * or use the overloaded method call without this parameter.
     *
     * @param cvTemplate The CVTemplate object containing the data to be populated in the Word document.
     * @param outputPath The path where the generated Word document should be saved.
     * @param createPdfLater Indicates if this document will be used to create a PDF in the next step.
     * @throws IOException If an I/O error occurs while reading the template or writing the output file.
     */
    public static void saveObjectAsWord(CVTemplate cvTemplate, String outputPath, boolean createPdfLater) throws IOException {

        Map<String, String> cVHashMap = HelperClass.convertCVObjectToHashMap(cvTemplate);
        //System.out.println(cVHashMap);
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
             //   "./src/main/resources/com/fdmgroup/cvgeneratorgradle/templates/fdm_cv_template_test4.docx";
                "./src/main/resources/com/fdmgroup/cvgeneratorgradle/templates/fdm_cv_template_international_v1.docx";

        try (FileInputStream templateInputStream = new FileInputStream(new File(wordTemplatePath));
             XWPFDocument document = new XWPFDocument(templateInputStream);
             OutputStream outputStream = new FileOutputStream(outputPath)) {

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

            // Replace placeholders in headers
            for (XWPFHeader header : document.getHeaderList()) {
                for (XWPFParagraph paragraph : header.getParagraphs()) {
                    replaceTextInParagraph(paragraph, cVHashMap);
                }
                for (XWPFTable table : header.getTables()) {
                    for (XWPFTableRow row : table.getRows()) {
                        for (XWPFTableCell cell : row.getTableCells()) {
                            for (XWPFParagraph paragraph : cell.getParagraphs()) {
                                replaceTextInParagraph(paragraph, cVHashMap);
                            }
                        }
                    }
                }
            }

            // Replace placeholders in footers
            for (XWPFFooter footer : document.getFooterList()) {
                for (XWPFParagraph paragraph : footer.getParagraphs()) {
                    replaceTextInParagraph(paragraph, cVHashMap);
                }
                for (XWPFTable table : footer.getTables()) {
                    for (XWPFTableRow row : table.getRows()) {
                        for (XWPFTableCell cell : row.getTableCells()) {
                            for (XWPFParagraph paragraph : cell.getParagraphs()) {
                                replaceTextInParagraph(paragraph, cVHashMap);
                            }
                        }
                    }
                }
            }


            String replacementString = "[MISSING]";//string used to replace leftover placeholders in the docx file
            //HelperClass.debugParagraphs(document);
            //HelperClassDocxCreation.displayTableContent(document);
            HelperClassDocxCreation.replaceNotFoundPlaceholders(document, replacementString);//change remaining placeholders to chosen string
            //HelperClassDocxCreation.removeEmptyRows(document);
            //HelperClassDocxCreation.displayTableContent(document);



            HelperClassDocxCreation.removeTablesWithNoData(document,replacementString);
            HelperClassDocxCreation.removeParagraphsWithSearchString(document, replacementString);
            //HelperClassDocxCreation.removeConsecutiveEmptyParagraphs(document);
            //HelperClassDocxCreation.printDocumentElements(document);
            if (createPdfLater) {//fix row heights, if this docx file is created to generate a pdf out of it.
                HelperClassDocxCreation.calculateAndSetTableRowHeights(document);
            }
            // Write the modified document to the output stream
            document.write(outputStream);
            System.out.println("word file saved");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Method to replace placeholders in a paragraph with user data.
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
     */
    private static void replaceTextInParagraph(XWPFParagraph paragraph, Map<String, String> replacements) {

        // Create a new run with the updated text
        String text = paragraph.getText();
        if (text.contains("{") && text.contains("}")) {

            //call helper methods:
            List<Integer> statusList = HelperClassDocxCreation.analyzeRuns(paragraph);
            List<Boolean> runContainsPlaceholder = HelperClassDocxCreation.rearrangeRuns(paragraph, statusList);

            // for debugging: Check if the number of runs matches the size of runContainsPlaceholder
            if (paragraph.getRuns().size() != runContainsPlaceholder.size()) {
                throw new IllegalStateException("Mismatch between the number of runs and runContainsPlaceholder list.");
            }

            // Iterate over the runs and replace placeholders if possible
            boolean alreadyShown = false;//We use this to display every warning only once and do not spam the console
            for (int i = 0; i < paragraph.getRuns().size(); i++) {
                XWPFRun run = paragraph.getRuns().get(i);
                String runText = run.getText(0);
                if (runText == null){
                    continue;
                }

                //check for debugging purposes only:
                if (!alreadyShown && !runContainsPlaceholder.get(i) && runText.contains("{")) {
                    System.out.println();
                    System.out.println("***********WRONG VALUE IN RUNCONTAINSPLACEHOLDER LIST!!!!!!!!**************");
                    System.out.println("(Don't worry: the error is intercepted and has no effect!");
                    HelperClassDocxCreation.printRunsWithIndex(paragraph);
                    System.out.println("### " + runContainsPlaceholder);
                    System.out.println();
                    alreadyShown = true;
                }

                //since the runContainsPlaceholder list contains wrong values, we just check the run text for brackets...
                if (/*runContainsPlaceholder.get(i)*/ runText.contains("{") && runText.contains("}")) {
                    for (Map.Entry<String, String> entry : replacements.entrySet()) {
                        String key = entry.getKey();
                        if (runText.contains(key)) {
                            String replacementValue = entry.getValue();

                            if ((key.contains("startDate") || key.contains("endDate"))) {//reformatting dates
                                    replacementValue =
                                            HelperClassDocxCreation.changeDateFormat
                                                    (replacementValue, "yyyy-MM-dd", "MMM yyyy");
                            }
                            run.setText(runText.replace(key, replacementValue), 0);
                        }
                    }
                }
            }
        }
    }

    /**
     * Generates a PDF document from a given CVTemplate object and saves it to the specified output path.
     *
     * <p>This method first generates a temporary Word document using the {@link #saveObjectAsWord(CVTemplate, String, boolean)}
     * method, and then converts the Word document to a PDF file using the Apache POI XWPF Converter library.</p>
     *
     * @param cvTemplate The CVTemplate object containing the data to be populated in the PDF document.
     * @param outputPath The path where the generated PDF document should be saved.
     * @throws IOException If an I/O error occurs while reading/writing files or during the conversion process.
     */
    public static void saveObjectAsPDF(CVTemplate cvTemplate, String outputPath) throws IOException {
        String wordTempPath = System.getProperty("java.io.tmpdir") + "tempCvDocument.docx";
        saveObjectAsWord(cvTemplate, wordTempPath, true);

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

}
