package com.fdmgroup.cvgeneratorgradle.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class Education {
	private int id;

	private String degree;

	private String studyTitle;

	private String universityName;

	private String universityPlace;

	private String startDate;

	private String endDate;

	private String thesisTitle;

	private List<String> keyModules;


	public Education(String degree, String studyTitle, String universityName, String universityPlace, String startDate,
			String endDate, String thesisTitle, List<String> keyModules) {
		this.degree = degree;
		this.studyTitle = studyTitle;
		this.universityName = universityName;
		this.universityPlace = universityPlace;
		this.startDate = startDate;
		this.endDate = endDate;
		this.thesisTitle = thesisTitle;
		this.keyModules = keyModules;
	}

}
