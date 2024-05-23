package com.fdmgroup.cvgeneratorgradle.utils;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class WordDocumentCreatorTest {
    public static void main(String[] args) throws Exception {

        //####create directory for saving and filepath:############
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

        String outpathWord = documentsPath.resolve("TestDocumentCreation.docx").toString();
        System.out.println("Word file path: " + outpathWord);
        //#################################

        XWPFDocument document = new XWPFDocument();

        // Create a table for the first experience
        XWPFTable table = document.createTable(5, 1); // 5 rows, 1 column
        setColumnWidth(table, 9000); // Set column width to 100%

        // Add content to the table
        createTableHeader(table.getRow(0), "Job Title");
        createTableRow(table.getRow(1), "Company Name | Start Date -- End Date");
        createTableRow(table.getRow(2), "Description: A short paragraph to describe your role and responsibilities, anything that makes you stand out.");
        createBulletedListRow(table.getRow(3), List.of("Text1/list key skills", "{Text2/list key skills}", "Text3/list key skills", "{Text4/list key skills}"));
        table.getRow(4).getCell(0).setText(""); // Empty row

        // Create a new paragraph for spacing
        document.createParagraph().createRun().addBreak(BreakType.TEXT_WRAPPING);

        // Create a table for the second experience
        table = document.createTable(5, 1); // 5 rows, 1 column
        setColumnWidth(table, 9000); // Set column width to 100%

        // Add content to the table
        createTableHeader(table.getRow(0), "{Job Title}");
        createTableRow(table.getRow(1), "{Company Name} | {Feb 2023} -- {Feb 2024}");
        createTableRow(table.getRow(2), "Description: {A short paragraph to describe your role and responsibilities, anything that makes you stand out.}");
        createBulletedListRow(table.getRow(3), List.of("{Text/list key skills}", "{Text/list key skills}", "{Text/list key skills}", "{Text/list key skills}"));
        table.getRow(4).getCell(0).setText(""); // Empty row

        // Save the document
        FileOutputStream out = new FileOutputStream(outpathWord);
        document.write(out);
        out.close();
    }

    private static void setColumnWidth(XWPFTable table, int width) {
        CTTblWidth tblWidth = table.getCTTbl().addNewTblPr().addNewTblW();
        tblWidth.setW(BigInteger.valueOf(width));
    }

    private static void createTableHeader(XWPFTableRow row, String text) {
        XWPFTableCell cell = row.getCell(0);
        XWPFParagraph paragraph = cell.addParagraph();
        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        run.setText(text);
    }

    private static void createTableRow(XWPFTableRow row, String text) {
        XWPFTableCell cell = row.getCell(0);
        XWPFParagraph paragraph = cell.addParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
    }

    private static void createBulletedListRow(XWPFTableRow row, List<String> items) {
        XWPFTableCell cell = row.getCell(0);
        XWPFParagraph paragraph = cell.addParagraph();
        XWPFRun run = paragraph.createRun();
        for (String item : items) {
            run.addBreak(BreakType.TEXT_WRAPPING);
            run.setText("\u2022 " + item);
        }
    }



}
