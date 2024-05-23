package com.fdmgroup.cvgeneratorgradle.utils;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//this class contains helper methods used for generating the CV output in docx format with desired data and formatting;
public class HelperClassDocxCreation {

    /**
     * Analyzes the runs (text segments) within a paragraph and creates a list of integers representing
     * the status of each run. The status indicates whether the run contains an opening bracket, a closing bracket,
     * both brackets, or no brackets.
     *
     * @param paragraph The XWPFParagraph object to be analyzed
     * @return A list of integers representing the status of each run within the paragraph
     */
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
                statusList.add(0); //run contains no bracket
            } else if (size == 1) { //in case we only have one bracket
                    if (bracketList.get(0) == 1) {
                        statusList.add(1);//run contains single opening bracket
                    } else if (bracketList.get(0) == 2) {
                        statusList.add(2);// run contains single closing
                    }
            } else if (bracketList.get(0) == 1 && bracketList.get(size - 1) == 2) {
                statusList.add(0);//run contains balanced brackets
            } else if (bracketList.get(0) == 1) {//so the last entry must be 1 if this is reached
                statusList.add(1);// run closes with opening bracket
            } else if (bracketList.get(0) == 2 && bracketList.get(size - 1) == 1) {
                statusList.add(3);// treated same as status 1
            } else if (bracketList.get(0) == 2) {
                statusList.add(2);
            }
        }

        return statusList;
    }


    /**
     * Rearranges the runs within a paragraph based on the provided status list. Runs containing opening brackets
     * are merged with subsequent runs until a closing bracket is encountered (so that placeholders texts distributed between
     * multiple runs are moved in only one run). The method returns a list indicating
     * whether each run contains one or more placeholders or not.
     *
     * @param paragraph  The XWPFParagraph object whose runs need to be rearranged
     * @param statusList The list of integers representing the status of each run within the paragraph
     * @return A list of booleans indicating whether each run contains one or more placeholders2 or not
     * @throws IllegalStateException If an invalid bracket sequence is encountered (e.g., closing bracket without an opening bracket)
     */
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
            } else if (status == 2) {
                throw new IllegalStateException("Invalid bracket sequence: closing bracket without opening one.");
            } else {
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
                index++;
            }
        }
        return placeholderExistsList;
    }

    /**
     * Debugs the paragraphs within an XWPFDocument by printing out the paragraphs that contain placeholders
     * and the text of each run within those paragraphs.
     *
     * @param document The XWPFDocument object containing the paragraphs to be debugged
     */
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

    /**
     * This method was replaced by replaceNotFoundPlaceholder (and the deletion method removeParagraphsWithSearchString in the second step
     * (but for now, only paragraphs with not found placeholders get removed. Inside the document, only a mark is set for not foundplaceholders))
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
    public static void removeParagraphsWithPlaceholders(XWPFDocument document) {
        List<XWPFParagraph> paragraphsToRemove = new ArrayList<>();
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            String text = paragraph.getText();
            if (text.contains("{") && text.contains("}")) {
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
                            //System.out.println("~~ Table paragraph to remove: " + text);
                        }
                    }
                }
            }
        }

        for (XWPFParagraph paragraph : paragraphsToRemove) {
            //System.out.println("~~~ Paragraphs that are removed: " + paragraph.getText());
            document.removeBodyElement(document.getPosOfParagraph(paragraph));
        }

        //removing table paragraphs does not work, but deleting the text does work.
        for (XWPFParagraph paragraph : paragraphsToRemoveFromTable) {
            //System.out.println("~~~ Table Paragraphs that are removed: " + paragraph.getText());
            for (XWPFRun run : paragraph.getRuns()) {
                //System.out.println("old run text: " + run.getText(0));
                run.setText("", 0);
            }
        }

    }

    public static void replaceNotFoundPlaceholders(XWPFDocument document, String replacement) {
        // create pattern for this syntax {....}
        Pattern pattern = Pattern.compile("\\{[^}]*\\}");

        // iterate through paragraphs in document
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replaceTextForNotFoundPlaceholders(paragraph, pattern, replacement);
        }

        // iterate through all paragraphs in tables
        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        replaceTextForNotFoundPlaceholders(paragraph, pattern, replacement);
                    }
                }
            }
        }
    }

    private static void replaceTextForNotFoundPlaceholders(XWPFParagraph paragraph, Pattern pattern, String replacement) {
        for (XWPFRun run : paragraph.getRuns()) {
            String runText = run.getText(0);
            if (runText != null) {
                Matcher matcher = pattern.matcher(runText);
                StringBuffer newText = new StringBuffer();

                while (matcher.find()) {
                    matcher.appendReplacement(newText, replacement);
                }
                matcher.appendTail(newText);

                run.setText(newText.toString(), 0);
            }
        }
    }

    // a table that contains the searchString in all cells is removed, since the data are missing.
    public static void removeTablesWithNoData(XWPFDocument document, String searchString) {
        List<XWPFTable> tablesToRemove = new ArrayList<>();
        for (XWPFTable table : document.getTables()) {
            if (isTableContainingStringInAllCells(table, searchString)) {
                tablesToRemove.add(table);
            }
        }
        for (XWPFTable table : tablesToRemove) {
            int pos = document.getPosOfTable(table);
            if (pos != -1) {
                document.removeBodyElement(pos);
            }
        }
    }

    private static boolean isTableContainingStringInAllCells(XWPFTable table, String searchString) {
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                if (!cell.getText().contains(searchString)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void removeParagraphsWithSearchString(XWPFDocument document, String searchString) {

        // Remove paragraphs in the document body
        /*
        List<XWPFParagraph> paragraphsToRemove = new ArrayList<>();
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            if (paragraph.getText().contains(searchString)) {
                paragraphsToRemove.add(paragraph);
            }
        }
        for (XWPFParagraph paragraph : paragraphsToRemove) {
            int pos = document.getPosOfParagraph(paragraph);
            if (pos != -1) {
                document.removeBodyElement(pos);
            }
        }
        */
        // Remove paragraphs in tables
        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    List<XWPFParagraph> paragraphs = cell.getParagraphs();
                    List<Integer> indicesToRemove = new ArrayList<>();
                    for (int i = 0; i < paragraphs.size(); i++) {
                        if (paragraphs.get(i).getText().contains(searchString)) {
                            indicesToRemove.add(i);
                        }
                    }
                    for (int i = indicesToRemove.size() - 1; i >= 0; i--) {
                        cell.removeParagraph(indicesToRemove.get(i));
                    }
                    if (cell.getParagraphs().isEmpty()) {
                        XWPFParagraph placeholderParagraph = cell.addParagraph();
                        placeholderParagraph.createRun().setText(" ");
                    }
                }
            }
        }
    }

    public static void printRunsWithIndex(XWPFParagraph paragraph) {
        System.out.println("#### Paragraph text: " + paragraph.getText());
        for (int i = 0; i < paragraph.getRuns().size(); i++) {
            XWPFRun run = paragraph.getRuns().get(i);
            System.out.println("#### Run index: " + i + ", Run text: " + run.getText(0));
        }
    }

    public static void displayTableContent(XWPFDocument document) {
        List<XWPFTable> tables = document.getTables();
        for (int t = 0; t < tables.size(); t++) {
            XWPFTable table = tables.get(t);
            System.out.println("-------Table " + (t + 1) + ":------------");
            for (int r = 0; r < table.getRows().size(); r++) {
                XWPFTableRow row = table.getRow(r);
                System.out.println("\tRow " + (r + 1) + ":");
                for (int c = 0; c < row.getTableCells().size(); c++) {
                    XWPFTableCell cell = row.getCell(c);
                    System.out.println("\t\tCell " + (c + 1) + ": " + getCellText(cell));
                }
            }
        }
    }

    private static String getCellText(XWPFTableCell cell) {
        StringBuilder cellText = new StringBuilder();
        for (XWPFParagraph paragraph : cell.getParagraphs()) {
            for (XWPFRun run : paragraph.getRuns()) {
                cellText.append(run.getText(0));
            }
        }
        return cellText.toString().trim();
    }


    /**
     * Calculates the height of each row in a table based on the content of the cells.
     *
     * @param document The XWPFDocument containing the table.
     */
    public static void calculateAndSetTableRowHeights(XWPFDocument document) {
        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                int rowHeight = calculateRowHeight(row);
                row.setHeight(rowHeight);
                centerTextVerticallyInRow(row);
            }
        }
    }


    /**
     * Sets the vertical alignment of text in each cell of a table row to center.
     *
     * @param row The XWPFTableRow to set the vertical alignment for.
     */
    private static void centerTextVerticallyInRow(XWPFTableRow row) {
        for (XWPFTableCell cell : row.getTableCells()) {
            CTTc ctTc = cell.getCTTc();
            CTTcPr tcPr = ctTc.isSetTcPr() ? ctTc.getTcPr() : ctTc.addNewTcPr();
            CTVerticalJc vAlign = tcPr.isSetVAlign() ? tcPr.getVAlign() : tcPr.addNewVAlign();
            vAlign.setVal(STVerticalJc.CENTER);
        }
    }


    /**
     * Calculates the height of a table row based on the content of its cells.
     *
     * @param row The XWPFTableRow to calculate the height for.
     * @return The calculated height in twips (twentieth of a point).
     */
    private static int calculateRowHeight(XWPFTableRow row) {
        int maxHeight = 0;
        for (XWPFTableCell cell : row.getTableCells()) {
            int cellHeight = calculateCellHeight(cell);
            if (cellHeight > maxHeight) {
                maxHeight = cellHeight;
            }
        }
        return maxHeight;
    }

    /**
     * Calculates the height of a table cell based on its content.
     *
     * @param cell The XWPFTableCell to calculate the height for.
     * @return The calculated height in twips (twentieth of a point).
     */
    private static int calculateCellHeight(XWPFTableCell cell) {
        int cellWidth = getCellWidth(cell);
        int totalHeight = 0;

        for (XWPFParagraph paragraph : cell.getParagraphs()) {
            int paragraphHeight = calculateParagraphHeight(paragraph, cellWidth);
            totalHeight += paragraphHeight;
        }

        return totalHeight;
    }

    /**
     * Calculates the height of a paragraph based on its content and the width of the cell.
     *
     * @param paragraph The XWPFParagraph to calculate the height for.
     * @param cellWidth The width of the cell in twips (twentieth of a point).
     * @return The calculated height in twips (twentieth of a point).
     */
    private static int calculateParagraphHeight(XWPFParagraph paragraph, int cellWidth) {
        int totalHeight = 0;

        for (XWPFRun run : paragraph.getRuns()) {
            String text = run.getText(0);
            int fontSize = run.getFontSize() > 0 ? run.getFontSize() : 12; // Default font size is 12
            int lineHeight = fontSize * 20 ; // Approximate line height in twips

            int textWidth = calculateTextWidth(text, fontSize);
            int lines = (int) Math.ceil((double) textWidth / cellWidth);

            totalHeight += lines * lineHeight;
        }

        return totalHeight;
    }

    /**
     * Calculates the width of the text based on its length and font size.
     *
     * @param text     The text to calculate the width for.
     * @param fontSize The font size of the text.
     * @return The calculated width in twips (twentieth of a point).
     */
    private static int calculateTextWidth(String text, int fontSize) {
        // Approximate character width in twips (twentieth of a point)
        int charWidth = fontSize * 10; // Adjust the multiplier based on the font and actual measurements
        return text.length() * charWidth;
    }

    /**
     * Gets the width of a table cell in twips (twentieth of a point).
     *
     * @param cell The XWPFTableCell to get the width for.
     * @return The width of the cell in twips (twentieth of a point).
     */
    private static int getCellWidth(XWPFTableCell cell) {
        // Approximate width of a table cell; adjust based on actual measurements or requirements
        return 5000; // Default width in twips (adjust as needed)
    }
}