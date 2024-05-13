package com.fdmgroup.cvgeneratorgradle.utils;

import org.apache.poi.xwpf.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReadWordDocument {

    public static void readWordDocument(String filePath) {
        try (InputStream fis = new FileInputStream(filePath);
             XWPFDocument document = new XWPFDocument(fis)) {
            System.out.println("==========content of the document is:==================");
            // Iterate through paragraphs and print text
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                System.out.println(paragraph.getText());
                String text = paragraph.getText();
            }

            System.out.println("============End of content=====================");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String filePath =  "C:/Users/Thomas/git/CV_Generator/cv-generator-executable-gradle/src/main/resources/com/fdmgroup/cvgeneratorgradle/templates/fdm_cv_template_test.docx";
        readWordDocument(filePath);
    }
}

