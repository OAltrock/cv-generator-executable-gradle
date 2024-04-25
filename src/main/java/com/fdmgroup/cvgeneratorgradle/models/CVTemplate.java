package com.fdmgroup.cvgeneratorgradle.models;

/*import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;*/

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CVTemplate is the class with the most logic and where most other objects in
 * the backend come together.
 * In CVTemplate the data of the object a CV is represented.
 * 
 * The methods are mostly getters an setters, except for the methods creating CV
 * files.
 * For the creation of the CV files Apache POI was used and for PDF
 * fr.opensagres.xdocreport was used,
 * which is able to convert a Apache POI XWPFDocument object to a PDF file.
 * 
 */
@Getter
@Setter
@NoArgsConstructor
public class CVTemplate {

	private int id;

	private User user;

	private String profile;

	private Location location;

	private Stream stream;

	private List<Experience> experiences;

	private List<Education> educations;

	private List<String> competences;

	private List<Certificate> certificates;

	private List<Language> languages;

	private List<Interest> interests;

	public CVTemplate(User user, String firstName, String lastName, Location location, Stream stream,
			ProfilePicture profilePicture, List<Experience> experience, List<Education> education,
			List<String> competence, List<Certificate> certificate, List<Language> language,
			List<Interest> interest) {
		this.user = user;
		this.location = location;
		this.stream = stream;
		this.experiences = experience;
		this.educations = education;
		this.competences = competence;
		this.certificates = certificate;
		this.languages = language;
		this.interests = interest;
	}
	
	//_____________________________________________________________________________________________

	/**
	 * Method to be called by a Controller. It chooses depending on the given string
	 * if the generated file should be a PDF or word file.
	 * It calls respectively the method generateWord() or generatePDF(). These
	 * methods then return a byte array which contains file as bytes.
	 * 
	 * @param format The format string decides whether a PDF or word file is
	 *               generated. Can only be "Word" or "PDF". Everything else results
	 *               in an error being thrown.
	 * @return returns an byte array containing the file converted to bytes.
	 * @throws IOException Throws an exception, if @param format receives an illegal
	 *                     string. Format can only "Word" or "PDF".
	 */
	/*public byte[] generateDocument(String format) throws IOException {
		if ("Word".equals(format)) {
			return generateWord();
		} else if ("PDF".equals(format)) {
			return generatePDF();
		}

		// Handle unsupported format or other cases
		throw new IllegalArgumentException("Unsupported document format: " + format);
	}*/

	/**
	 * This methods loads from src/main/resources/files the template file
	 * corresponding to the stream and location (choosing stream and location not
	 * yet properly implemented).
	 * The logic behind this method is to first choose which template word file to
	 * use and load it. After that a XWPFDocument (Apache POI) is created. This
	 * loaded file uses a table for formatting because of that we have to first
	 * iterate through the whole table.
	 * After the table we iterate through the paragraph inside of the single cells
	 * inside of the table. On every paragraph that is found the method
	 * checkParagraphForAction() is called.
	 * This method will change the id in the template file to the actual value that
	 * is in the CVTemplate object.
	 * Also if the CVTemplate object has more than 3 experiences or educations these
	 * will be ignored. The template file decides how many things will be possible
	 * to place.
	 * 
	 * @return Returns a byte array of the generated file.
	 * @throws IOException Throws an IOException if the template file is not found.
	 */
	/*public byte[] generateWord() throws IOException {
		// Create a ByteArrayOutputStream to store the document as bytes
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			// FOR NOW GENERATES ONLY INTERNATIONAL
			try (InputStream inputStream = getClass().getClassLoader()
					.getResourceAsStream("files/templates/fdm_cv_template_v1.docx")) {
				XWPFDocument doc = new XWPFDocument(inputStream);

				boolean rowShouldbreak = false;

				// The loop to iterate over the template word file. It needs to check every
				// paragraph to check for a possible action.
				// The loop iterating over the rows has to be skipped if something has to be
				// removed from the template file. For example if there are only 2 experiences
				// in the CVTemplate object there has to be one experience removed from the CV.
				for (XWPFTable tbl : doc.getTables()) {
					rowLoop: for (int i = 0; i < tbl.getRows().size(); i++) {
						XWPFTableRow row = tbl.getRows().get(i);

						for (XWPFTableCell cell : row.getTableCells()) {
							for (int j = 0; j < cell.getParagraphs().size(); j++) {
								XWPFParagraph paragraph = cell.getParagraphs().get(j);

								rowShouldbreak = checkParagraphForAction(paragraph, cell, row, tbl, rowShouldbreak);

								if (rowShouldbreak) {
									rowShouldbreak = false;
									i--;
									continue rowLoop;
								}
							}
						}
					}
				}
				doc.write(byteArrayOutputStream); // save the document to the ByteArrayOutputStream
				doc.close();
			}
			return byteArrayOutputStream.toByteArray();
		}
	}*/

	/**
	 * Method to check what action should be used for each id in a paragraph. Uses
	 * regex to find multiple instances of a id.
	 * Gives all parameters of where a paragraph is to be able to remove it
	 * properly. The boolean rowShouldbreak is used to state if in a previous
	 * iteration something was deleted.
	 * Calls the method
	 * 
	 * @param paragraph      The paragraph inside of a cell containing ids.
	 * @param cell           The cell which can contain multiple paragraphs.
	 * @param row            The row which can contain multiple cells.
	 * @param tbl            The table which can contain multiple rows.
	 * @param rowShouldbreak A boolean to show if in previous iteration something
	 *                       was deleted to avoid accessing wrong objects after
	 *                       deletion.
	 * @return Returns the boolean rowShouldBreak to show the calling instances if
	 *         something needs to change in loop.
	 */
	/*private boolean checkParagraphForAction(XWPFParagraph paragraph, XWPFTableCell cell, XWPFTableRow row,
			XWPFTable tbl, boolean rowShouldbreak) {

		int experiencesSize = this.getExperiences().size();
		int educationSize = this.getEducations().size();
		int presetCompetencesSize = this.getStream().getPresetCompetences().size();
		int competencesSize = this.getCompetences().size();
		int certificatesSize = this.getCertificates().size();
		int languagesSize = this.getLanguages().size();
		int interestSize = this.getInterests().size();
		int trainingSize = this.getStream().getPresetTraining().size();

		String paragraphText = paragraph.getText();

		// REGEX pattern and matcher
		Pattern pattern;
		Matcher matcher;

		// picture:
		if (paragraphText.contains("Picture_1")) {
			replaceTextInParagraph("Picture_1", "", paragraph);

			try {
				XWPFRun run = paragraph.createRun();

				InputStream inputStreamFromDatabase = new ByteArrayInputStream(
						this.user.getProfilePicture().getBase64());

				int imageType = XWPFDocument.PICTURE_TYPE_JPEG;

				int width = 127;
				int height = 118;

				run.addPicture(inputStreamFromDatabase, imageType, "Profile Picture", Units.toEMU(width),
						Units.toEMU(height));
				paragraph.setAlignment(ParagraphAlignment.CENTER);
			} catch (Exception e) {
				System.out.println("ERROR IN PICTURE ADDING");
			}
		}

		// name
		if (paragraphText.contains("Firstname")) {
			replaceTextInParagraph("Firstname", this.user.getFirstName(), paragraph);
		}

		if (paragraphText.contains("Lastname")) {
			replaceTextInParagraph("Lastname", this.user.getLastName(), paragraph);
		}
		
		if (paragraphText.contains("Stream")) {
			String stream = this.getStream().getStreamName();
			replaceTextInParagraph("Stream", stream.substring(0, 1).toUpperCase() + stream.substring(1), paragraph);
		}

		// Experience
		pattern = Pattern.compile("ExperienceTitle_(\\d)");
		matcher = pattern.matcher(paragraphText);
		if (matcher.lookingAt()) {
			int expirienceKey = Integer.parseInt(matcher.group(1));
			if (experiencesSize >= expirienceKey) {
				replaceTextInParagraph(matcher.group(), this.getExperiences().get(expirienceKey - 1).getJobTitle(),
						paragraph);
			} else {
				int indexOfrow = tbl.getRows().indexOf(row);
				tbl.removeRow(indexOfrow);
				rowShouldbreak = true;
				return rowShouldbreak;
			}
		}

		pattern = Pattern.compile("Company_(\\d)");
		matcher = pattern.matcher(paragraphText);
		if (matcher.lookingAt()) {
			int expirienceKey = Integer.parseInt(matcher.group(1));
			if (experiencesSize >= expirienceKey) {
				replaceTextInParagraph(matcher.group(), this.getExperiences().get(expirienceKey - 1).getCompanyName(),
						paragraph);
			} else {
				int indexOfrow = tbl.getRows().indexOf(row);
				tbl.removeRow(indexOfrow);
				rowShouldbreak = true;
				return rowShouldbreak;
			}
		}

		pattern = Pattern.compile(".*(ExperienceLocation_(\\d))");
		matcher = pattern.matcher(paragraphText);
		if (matcher.lookingAt()) {
			int expirienceKey = Integer.parseInt(matcher.group(2));
			if (experiencesSize >= expirienceKey) {
				replaceTextInParagraph(matcher.group(1), this.getExperiences().get(expirienceKey - 1).getCompanyPlace(),
						paragraph);
			} else {
				replaceTextInParagraph(matcher.group(), "", paragraph);
			}
		}

		pattern = Pattern.compile("ExperienceDate_(\\d)");
		matcher = pattern.matcher(paragraphText);
		if (matcher.lookingAt()) {
			int expirienceKey = Integer.parseInt(matcher.group(1));
			if (experiencesSize >= expirienceKey) {
				replaceTextInParagraph(matcher.group(),
						this.getExperiences().get(expirienceKey - 1).getStartDate() + "-"
								+ this.getExperiences().get(expirienceKey - 1).getEndDate(),
						paragraph);
			} else {
				replaceTextInParagraph(matcher.group(), "", paragraph);
			}
		}

		// Experience bullet points
		pattern = Pattern.compile("Experience_(\\d)_(\\d)");
		matcher = pattern.matcher(paragraphText);
		if (matcher.lookingAt()) {
			int expirienceKey = Integer.parseInt(matcher.group(1));
			int keySkillKey = Integer.parseInt(matcher.group(2));
			if (experiencesSize >= expirienceKey
					&& this.getExperiences().get(expirienceKey - 1).getPositionFeatures().size() >= keySkillKey) {
				replaceTextInParagraph(matcher.group(),
						this.getExperiences().get(expirienceKey - 1).getPositionFeatures().get(keySkillKey - 1),
						paragraph);
			} else if (keySkillKey == 1) {
				int indexOfrow = tbl.getRows().indexOf(row);
				tbl.removeRow(indexOfrow);
				tbl.removeRow(indexOfrow);
				rowShouldbreak = true;
				return rowShouldbreak;
			} else {
				cell.removeParagraph(cell.getParagraphs().indexOf(paragraph));
				return true;
			}
		}

		// Education
		pattern = Pattern.compile("EducationTitle_(\\d)");
		matcher = pattern.matcher(paragraphText);
		if (matcher.lookingAt()) {
			int educationKey = Integer.parseInt(matcher.group(1));
			if (educationSize >= educationKey) {
				replaceTextInParagraph(matcher.group(), this.getEducations().get(educationKey - 1).getStudyTitle(),
						paragraph);
			} else {
				int indexOfrow = tbl.getRows().indexOf(row);
				tbl.removeRow(indexOfrow);
				rowShouldbreak = true;
				return rowShouldbreak;
			}
		}

		pattern = Pattern.compile("University_(\\d)");
		matcher = pattern.matcher(paragraphText);
		if (matcher.lookingAt()) {
			int educationKey = Integer.parseInt(matcher.group(1));
			if (educationSize >= educationKey) {
				replaceTextInParagraph(matcher.group(), this.getEducations().get(educationKey - 1).getUniversityName(),
						paragraph);
			} else {
				int indexOfrow = tbl.getRows().indexOf(row);
				tbl.removeRow(indexOfrow);
				rowShouldbreak = true;
				return rowShouldbreak;
			}
		}

		pattern = Pattern.compile(".*(EducationLocation_(\\d))");
		matcher = pattern.matcher(paragraphText);
		if (matcher.lookingAt()) {
			int educationKey = Integer.parseInt(matcher.group(2));
			if (educationSize >= educationKey) {
				replaceTextInParagraph(matcher.group(1),
						this.getEducations().get(educationKey - 1).getUniversityPlace(),
						paragraph);
			} else {
				replaceTextInParagraph(matcher.group(), "", paragraph);
			}
		}

		pattern = Pattern.compile("EducationDate_(\\d)");
		matcher = pattern.matcher(paragraphText);
		if (matcher.lookingAt()) {
			int educationKey = Integer.parseInt(matcher.group(1));
			if (educationSize >= educationKey) {
				replaceTextInParagraph(matcher.group(), this.getEducations().get(educationKey - 1).getStartDate() + "-"
						+ this.getEducations().get(educationKey - 1).getEndDate(), paragraph);
			} else {
				replaceTextInParagraph(matcher.group(), "", paragraph);
			}
		}

		// Bullet points for education
		pattern = Pattern.compile("Education_(\\d)_(\\d)");
		matcher = pattern.matcher(paragraphText);
		if (matcher.lookingAt()) {
			int educationKey = Integer.parseInt(matcher.group(1));
			int keyModulesKey = Integer.parseInt(matcher.group(2));

			if (educationSize >= educationKey
					&& this.getEducations().get(educationKey - 1).getKeyModules().size() >= keyModulesKey - 1) {

				if (keyModulesKey == 1) {
					replaceTextInParagraph(matcher.group(), this.getEducations().get(educationKey - 1).getThesisTitle(),
							paragraph);
				} else {
					replaceTextInParagraph(matcher.group(),
							this.getEducations().get(educationKey - 1).getKeyModules().get(keyModulesKey - 2),
							paragraph);
				}
			} else if (keyModulesKey == 1) {
				int indexOfrow = tbl.getRows().indexOf(row);
				tbl.removeRow(indexOfrow);
				rowShouldbreak = true;
				return rowShouldbreak;
			} else {
				cell.removeParagraph(cell.getParagraphs().indexOf(paragraph));
				return true;
			}
		}
		
		// FDM Training
		pattern = Pattern.compile("Training_(\\d{1,2})");
		matcher = pattern.matcher(paragraphText);
		if (matcher.lookingAt()) {
			if (trainingSize >= Integer.parseInt(matcher.group(1))) {
				replaceTextInParagraph(matcher.group(),
						this.getStream().getPresetTraining().get(Integer.parseInt(matcher.group(1)) - 1),
						paragraph);
			} else {
				cell.removeParagraph(cell.getParagraphs().indexOf(paragraph));
				return true;
			}
		}
		
		// Preset competences
		pattern = Pattern.compile("PresetCompetence_(\\d)");
		matcher = pattern.matcher(paragraphText);
		if (matcher.lookingAt()) {
			if (presetCompetencesSize >= Integer.parseInt(matcher.group(1))) {
				replaceTextInParagraph(matcher.group(),
						this.getStream().getPresetCompetences().get(Integer.parseInt(matcher.group(1)) - 1),
						paragraph);
			} else {
				cell.removeParagraph(cell.getParagraphs().indexOf(paragraph));
				return true;
			}
		}

		// Competences
		pattern = Pattern.compile("Competence_(\\d)");
		matcher = pattern.matcher(paragraphText);
		if (matcher.lookingAt()) {
			if (competencesSize >= Integer.parseInt(matcher.group(1))) {
				replaceTextInParagraph(matcher.group(),
						this.getCompetences().get(Integer.parseInt(matcher.group(1)) - 1).getCompetenceName(),
						paragraph);
			} else {
				cell.removeParagraph(cell.getParagraphs().indexOf(paragraph));
				return true;
			}
		}

		// Certificates
		pattern = Pattern.compile("Certificate_(\\d)");
		matcher = pattern.matcher(paragraphText);
		if (matcher.lookingAt()) {
			if (certificatesSize >= Integer.parseInt(matcher.group(1))) {
				replaceTextInParagraph(matcher.group(),
						this.getCertificates().get(Integer.parseInt(matcher.group(1)) - 1).getCertificateName(),
						paragraph);
			} else {
				cell.removeParagraph(cell.getParagraphs().indexOf(paragraph));
				return true;
			}
		}

		// Languages
		pattern = Pattern.compile("NiveauLanguage_(\\d)");
		matcher = pattern.matcher(paragraphText);
		if (matcher.lookingAt()) {
			if (languagesSize >= Integer.parseInt(matcher.group(1))) {
				replaceTextInParagraph(matcher.group(),
						this.getLanguages().get(Integer.parseInt(matcher.group(1)) - 1).getLanguageType() + ": "
								+ this.getLanguages().get(Integer.parseInt(matcher.group(1)) - 1).getLanguageLevel(),
						paragraph);
			} else {
				cell.removeParagraph(cell.getParagraphs().indexOf(paragraph));
				return true;
			}
		}

		// Interests
		pattern = Pattern.compile("Interest_(\\d)");
		matcher = pattern.matcher(paragraphText);
		if (matcher.lookingAt()) {
			if (interestSize >= Integer.parseInt(matcher.group(1))) {
				replaceTextInParagraph(matcher.group(),
						this.getInterests().get(Integer.parseInt(matcher.group(1)) - 1).getInterestName(), paragraph);
			} else {
				cell.removeParagraph(cell.getParagraphs().indexOf(paragraph));
				return true;
			}
		}

		return false;
	}*/

	/**
	 * Method that replaces the given key (id) with the actual data from the object
	 * (content).
	 * All paragraphs poses runs, which save formatting data. These need to be
	 * removed and new one needs to be created.
	 * To add to this all text formatting like for example setting something bold is
	 * done here.
	 * 
	 * @param key       The key in the template file.
	 * @param content   The content that should replace the key in the file.
	 * @param paragraph The paragraph object containing the Apache POI data.
	 */
	private void replaceTextInParagraph(String key, String content, XWPFParagraph paragraph) {
		String paragraphText = paragraph.getParagraphText();
		if (paragraphText.contains(key)) {
			String updatedParagraphText = paragraphText.replace(key, content);
			while (paragraph.getRuns().size() > 0) {
				paragraph.removeRun(0);
			}
			XWPFRun newRun = paragraph.createRun();
			newRun.setText(updatedParagraphText);
			if (key.matches("EducationDate_\\d") || key.matches("ExperienceDate_\\d") || key.matches("Firstname")
					|| key.matches("Lastname") || key.matches("ExperienceTitle_\\d")
					|| key.matches("EducationTitle_\\d")) {
				newRun.setBold(true);
			}

		}
	}

	/**
	 * This methods loads from src/main/resources/files the template file
	 * corresponding to the stream and location (choosing stream and location not
	 * yet properly implemented).
	 * The logic behind this method is to first choose which template word file to
	 * use and load it. After that a XWPFDocument (Apache POI) is created. This
	 * loaded file uses a table for formatting because of that we have to first
	 * iterate through the whole table.
	 * After the table we iterate through the paragraph inside of the single cells
	 * inside of the table. On every paragraph that is found the method
	 * checkParagraphForAction() is called.
	 * This method will change the id in the template file to the actual value that
	 * is in the CVTemplate object.
	 * Also if the CVTemplate object has more than 3 experiences or educations these
	 * will be ignored. The template file decides how many things will be possible
	 * to place.
	 * The only difference to generateWord() is that this method converts the
	 * generated word file in the end to a PDF file with fr.opensagres.xdocreport
	 * (This method should be changed in the future).
	 * 
	 * @return Returns a byte array of the generated file.
	 * @throws IOException Throws an IOException if the template file is not found.
	 */
	/*public byte[] generatePDF() throws IOException {
		// Create a ByteArrayOutputStream to store the document as bytes
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			// FOR NOW GENERATES ONLY INTERNATIONAL
			try (InputStream inputStream = getClass().getClassLoader()
					.getResourceAsStream("files/templates/fdm_cv_template_v1.docx")) {
				XWPFDocument doc = new XWPFDocument(inputStream);

				boolean rowShouldbreak = false;

				for (XWPFTable tbl : doc.getTables()) {
					rowLoop: for (int i = 0; i < tbl.getRows().size(); i++) {
						XWPFTableRow row = tbl.getRows().get(i);

						for (XWPFTableCell cell : row.getTableCells()) {
							for (int j = 0; j < cell.getParagraphs().size(); j++) {
								XWPFParagraph paragraph = cell.getParagraphs().get(j);

								rowShouldbreak = checkParagraphForAction(paragraph, cell, row, tbl, rowShouldbreak);

								if (rowShouldbreak) {
									rowShouldbreak = false;
									i--;
									continue rowLoop;
								}
							}
						}
					}
				}
				// the generated word document is converted to a pdf file and written to the
				// ByteArrayOutputStream
				PdfOptions pdfOptions = PdfOptions.create();
				PdfConverter.getInstance().convert(doc, byteArrayOutputStream, pdfOptions); // save the document to the
																							// ByteArrayOutputStream
				doc.close();
			}
			return byteArrayOutputStream.toByteArray();
		}

	}*/

	@Override
	public String toString() {
		return "CVTemplate [id=" + id + ", firstName=" + user.getFirstName() + ", lastName=" + user.getLastName() + "]";
	}

}
