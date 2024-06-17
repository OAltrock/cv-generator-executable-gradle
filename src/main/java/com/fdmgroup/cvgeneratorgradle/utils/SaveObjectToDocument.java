package com.fdmgroup.cvgeneratorgradle.utils;

import com.fdmgroup.cvgeneratorgradle.Main;
import com.fdmgroup.cvgeneratorgradle.controller.MainController;
import com.fdmgroup.cvgeneratorgradle.controller.ProfileController;
import com.fdmgroup.cvgeneratorgradle.controller.SummaryController;
import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;

import com.fdmgroup.cvgeneratorgradle.views.FDMCenterVBoxWrapper;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.poi.xwpf.usermodel.*;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;

import java.io.*;
import java.util.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * In English:
 * This class (including the helper classes) contains the code for document creation (PDF, Docx).
 * The creation of the docx file is initiated by the method: saveObjectAsWord(...), the PDF file is generated accordingly via saveObjectAsPDF(...).
 * We used createDocument(...) as a kind of wrapper method that is called by the UI.
 * It calls saveObjectAsWord(...) or saveObjectAsPdf(...) depending on the case and performs certain additional operations.

 * The arguments [BorderPane main, TreeView<String> treeView, Stage stage, Menu recent, MainController mainController] are used to return a status message to the UI.
 * Oliver Altrock can provide more detailed information.

 *  Additional (hopefully useful) information
 *  we first create a HashMap from the cvTemplate object (Helperclass.convertCVObjectToHashMap(...)) to avoid problems with the object structure later on.
 *  In this method, we define unique placeholders for each variable that we take from the cvTemplate object.
 *  If variables of the cvTemplate object are changed or added later (by the developer), a change must also be made in this method (Helperclass.convertCVObjectToHashMap(...)).
 *  New placeholders can be created and used in the docx template in this way.
 *  The names of the placeholders follow the variable names in the cvTemplate object and are marked with {...}. indices in [..] are used for lists.
 *  An example list is saved in resources/templates/placeholderExamples.txt.

 *  The class utils/CVTemplateExampleValues was written for testing and debugging purposes.


 * In German:
 * Diese Klasse (einschließlich der helper Klassen) enthält den Code zu Dokumentenerstellung (PDF, Docx).
 * Das erstellen der docx Datei wird initiiert durch die Methode: saveObjectAsWord(...), die PDF Datei wird entsprechnend generiert über saveObjectAsPDF(...).
 * Wir benutzten createDocument(...) als eine Art wrapper Methode, die von der UI aufgerufen wird,
 * je nach Fall saveObjectAsWord(...) oder saveObjectAsPdf(...) aufruft und gewisse zusatz Operationen durchführt.

 * Die Argumente [BorderPane main, TreeView<String> treeView, Stage stage, Menu recent, MainController mainController] dienen dazu eine Statusmeldung an das UI zurückzugeben.
 * genauere Information kann Oliver Altrock geben.

 * Zusätzliche (hoffentlich nützliche) Informationen
 * wir erzeugen zuerst eine HashMap aus dem cvTemplate Object (Helperclass.convertCVObjectToHashMap(...)) um spätere Probleme mit der Objektstrucktur zu vermeiden.
 * Wir definieren in dieser Methode eindeutige Placeholder für jede Variable, die wir aus dem cvTemplate object nehmen.
 * Sollten später Variablen des cvTemplate Objekts verändert oder hinzugefügt werden, muss auch eine Änderrung in dieser Methode erfolgen.
 * Neue Placeholder können so erzeugt und im docx Template verwendet werden.
 * Die Namen der Placeholder folgen den Varablennamen im cvTemplate Object und werden durch {...} markiert. für Listen werden indizes in  [..] verwendet.
 * Eine Beispielliste ist in resources/templates/placeholderExamples.txt gepeichert.

 * Zu test und debugging Zwecken wurde die Klasse utils/CVTemplateExampleValues geschrieben.
 *
 * @Author Thomas Elble, thomas.elble@fdmgroup.com
 * @Version 1.0 (14th, June, 2024)
 */
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
     * @throws IOException              If an I/O error occurs while creating the auto-save file or generating the document.
     * @throws IllegalArgumentException If an unsupported document format is provided.
     */
    public static void createDocument(CVTemplate cvTemplate, String format, String outputPath, BorderPane main,
                                      TreeView<String> treeView, Stage stage, Menu recent, MainController mainController) throws IOException {
        //creating an auto save in json format:
        String saveFilePath = SaveObjectToJson.savePath + File.separator + "autosave_fullCV.json";
        SaveObjectToJson.saveObjectAsJson(cvTemplate, saveFilePath);
        try {
            if ("docx".equalsIgnoreCase(format) || "word".equalsIgnoreCase(format)) {
                saveObjectAsWord(cvTemplate, outputPath, false, main);

                showConfirmation(cvTemplate, main, treeView, stage, recent, "docx", mainController);

            } else if ("PDF".equalsIgnoreCase(format)) {
                saveObjectAsPDF(cvTemplate, outputPath, main, treeView, stage, recent);
                showConfirmation(cvTemplate, main, treeView, stage, recent, "PDF", mainController);
            } else {
                throw new IllegalArgumentException("Unsupported document format: " + format);
            }
        } catch (InterruptedException e) {
            try {
                showConfirmation(cvTemplate, main, treeView, stage, recent, e.getMessage(), mainController);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    // overloaded method, save docx file in home directory:
    public static void saveObjectAsWord(CVTemplate cvTemplate) throws IOException {
        String documentsFolderPath = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "CVgenerator";
        String outputPath = documentsFolderPath + File.separator + "CvAutoSave.docx";
        saveObjectAsWord(cvTemplate, outputPath);
    }

    /**
     * Overloaded method call to save the CVTemplate object as a Word (docx) document to the specified output path.
     *
     * @param cvTemplate The CVTemplate object containing the data to be populated in the Word document.
     * @param outputPath The path where the generated Word document should be saved.
     * @throws IOException If an I/O error occurs while saving the document.
     */
    public static void saveObjectAsWord(CVTemplate cvTemplate, String outputPath) throws IOException {
        saveObjectAsWord(cvTemplate, outputPath, false,  new BorderPane());
    }

    /**
     * Generates a Word document from a given CVTemplate object and saves it to the specified output path.
     * if {@param createPdfLater} is set to true, the table formatting is changed to improve table structure in the pdf document
     * (word by default uses automatic settings for e.g. table cell heights, which can not be read by the PDF libaries,
     * therefore, we have to set cell heights to an estimated fixed value).
     * if you don't want to use the Word file to create a pdf in a second step, set {@param createPdfLater} to false
     * or use the overloaded method call without this parameter.
     *
     * @param cvTemplate     The CVTemplate object containing the data to be populated in the Word document.
     * @param outputPath     The path where the generated Word document should be saved.
     * @param createPdfLater Indicates if this document will be used to create a PDF in the next step.
     * @throws IOException If an I/O error occurs while reading the template or writing the output file.
     */
    public static void saveObjectAsWord(CVTemplate cvTemplate, String outputPath, boolean createPdfLater, BorderPane main) throws IOException {

        Map<String, String> cVHashMap = HelperClass.convertCVObjectToHashMap(cvTemplate);
        //System.out.println(cVHashMap);
        HelperClass.printHashMap(cVHashMap);
        String documentsFolderPath = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "CVgenerator";
        //String outputPath = documentsFolderPath + File.separator + "CvAutoSave.docx";
        String outputPathTestFile = documentsFolderPath + File.separator + "paragraphsFound.txt";
        String outputPathTestFile2 = documentsFolderPath + File.separator + "replacementsRecord.txt";
        Label errLabel = new Label();
        errLabel.setWrapText(true);
        errLabel.setAlignment(Pos.TOP_LEFT);
        HBox container = new HBox(errLabel);


        // Create the output directory if it doesn't exist
        File outputDirectory = new File(documentsFolderPath);
        if (!outputDirectory.exists()) {
            boolean created = outputDirectory.mkdirs();
            if (!created) {
                errLabel.setText("Error:\n" + documentsFolderPath);
                main.setCenter(container);
            }
        }

        try {
            try (InputStream templateInputStream = Main.class
                    .getResourceAsStream("templates/fdm_cv_template_international_v1.docx")) {
                assert templateInputStream != null;
                try (XWPFDocument document = new XWPFDocument(templateInputStream);
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
                    //HelperClassDocxCreation.displayTableContent(document);

                    HelperClassDocxCreation.removeTablesWithNoData(document, replacementString);
                    HelperClassDocxCreation.removeParagraphsWithSearchStringFromTables(document, replacementString);

                    if (createPdfLater) {//fix row heights, if this docx file is created to generate a pdf out of it.
                        HelperClassDocxCreation.calculateAndSetTableRowHeights(document);
                    }
                    // Write the modified document to the output stream
                    document.write(outputStream);
                    System.out.println("word file saved");
                }
            }
        } catch (Exception e) {
            errLabel.setText("Error while trying to generate template:\n" +
                    e.getMessage());
            main.setCenter(container);
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
     * @param paragraph    The XWPFParagraph object representing the paragraph to process.
     * @param replacements A map containing the placeholders as keys and their replacement values as values.
     * @throws IllegalStateException If there is a mismatch between the number of runs and the size of the runContainsPlaceholder list.
     */
    private static void replaceTextInParagraph(XWPFParagraph paragraph, Map<String, String> replacements) {

        // Create a new run with the updated text
        String text = paragraph.getText();
        if (text.contains("{") && text.contains("}")) {

            //call helper methods:
            List<Integer> statusList = HelperClassDocxCreation.analyzeRuns(paragraph);
            List<Boolean> runContainsPlaceholder = HelperClassDocxCreation.rearrangeRuns(paragraph, statusList);//runContainsPlaceholder still contains wrong entries, so don't use it!!!!

            // for debugging: Check if the number of runs matches the size of runContainsPlaceholder
            //if (paragraph.getRuns().size() != runContainsPlaceholder.size()) {
            //    throw new IllegalStateException("Mismatch between the number of runs and runContainsPlaceholder list.");
            //}

            // Iterate over the runs and replace placeholders if possible
            boolean alreadyShown = false;//We use this to display every warning only once and do not spam the console
            for (int i = 0; i < paragraph.getRuns().size(); i++) {
                XWPFRun run = paragraph.getRuns().get(i);
                String runText = run.getText(0);
                if (runText == null) {
                    continue;
                }

                //check for debugging purposes only:
                if (false) {//deactivated part
                    if (!alreadyShown && !runContainsPlaceholder.get(i) && runText.contains("{")) {
                        System.out.println();
                        System.out.println("***********WRONG VALUE IN RUNCONTAINSPLACEHOLDER LIST!!!!!!!!**************");
                        System.out.println("(Don't worry: the error is intercepted and has no effect!");
                        HelperClassDocxCreation.printRunsWithIndex(paragraph);
                        System.out.println("### " + runContainsPlaceholder);
                        System.out.println();
                        alreadyShown = true;
                    }
                }

                //since the runContainsPlaceholder list contains wrong values, we just check the run text for brackets...
                if (/*runContainsPlaceholder.get(i)){*/ runText.contains("{") && runText.contains("}")) {
                    //find placeholder text in replacements HashMap
                    for (Map.Entry<String, String> entry : replacements.entrySet()) {
                        String key = entry.getKey();
                        if (runText.contains(key)) {
                            String replacementValue = entry.getValue();

                            if (key.contains("startDate") || key.contains("endDate")) {//reformatting dates
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

    //overloaded method, where you don't have to give the UI Arguments for user notifications
    public static void saveObjectAsPDF(CVTemplate cvTemplate, String outputPath) throws IOException {
        saveObjectAsPDF(cvTemplate, outputPath,  new BorderPane(), new TreeView<>(), new Stage(), new Menu());
    }

    /**
     * Generates a PDF document from a given CVTemplate object and saves it to the specified output path.
     *
     * <p>This method first generates a temporary Word document using the {@link #saveObjectAsWord(CVTemplate, String, boolean, BorderPane)}
     * method, and then converts the Word document to a PDF file using the Apache POI XWPF Converter library.</p>
     *
     * @param cvTemplate The CVTemplate object containing the data to be populated in the PDF document.
     * @param outputPath The path where the generated PDF document should be saved.
     * @throws IOException If an I/O error occurs while reading/writing files or during the conversion process.
     */
    public static void saveObjectAsPDF(CVTemplate cvTemplate, String outputPath, BorderPane main, TreeView<String> treeView, Stage stage, Menu recent) throws IOException {
        String wordTempPath = System.getProperty("java.io.tmpdir") + "tempCvDocument.docx";
        saveObjectAsWord(cvTemplate, wordTempPath, true, main);

        try (FileInputStream wordInputStream = new FileInputStream(new File(wordTempPath));
             XWPFDocument document = new XWPFDocument(wordInputStream);
             OutputStream pdfOutputStream = new FileOutputStream(outputPath)) {

            PdfOptions options = PdfOptions.create();
            PdfConverter.getInstance().convert(document, pdfOutputStream, options);

            System.out.println("PDF file saved");
        } catch (Exception e) {
            Label errLabel = new Label("Error:\n" +
                    e.getMessage());
            errLabel.setAlignment(Pos.CENTER);
            errLabel.setWrapText(true);
            HBox newHBox = new HBox(errLabel);
            main.setCenter(newHBox);
        } finally {
            System.out.println((new File(wordTempPath).delete() ? "deleted!" : wordTempPath + " couldn't be deleted"));
        }
    }

    private static void showConfirmation(CVTemplate cvTemplate, BorderPane main, TreeView<String> treeView, Stage stage, Menu recent, String type, MainController mainController) throws InterruptedException {
        Label errLabel = new Label();
        if (type.contains("docx") || type.contains("PDF")) {
             errLabel.setText(type + " successfully saved!");
        }
        else errLabel.setText("Error:\n"+type);
        errLabel.setWrapText(true);
        FDMCenterVBoxWrapper newHBox = new FDMCenterVBoxWrapper(errLabel);
        newHBox.setDesign();
        main.setCenter(newHBox);
        //TimeUnit.SECONDS.sleep(15);
        delay(5000, ()->new SummaryController(cvTemplate, treeView, stage, recent).initialize(main, mainController));
    }

    public static void delay(long millis, Runnable continuation) {
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try { Thread.sleep(millis); }
                catch (InterruptedException e) { }
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> continuation.run());
        new Thread(sleeper).start();
    }

}
