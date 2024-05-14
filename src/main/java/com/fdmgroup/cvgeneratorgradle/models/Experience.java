package com.fdmgroup.cvgeneratorgradle.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class Experience {

	private int id;

	private String jobTitle;

	private String startDate;

	private String endDate;

	private List<String> positionFeatures;

	private String companyName;

	private String companyPlace;

	private String description;
	
	
	public Experience(String jobTitle, String startDate, String endDate, List<String> positionsFeatures,
			String companyName, String companyPlace, String description) {
		this.jobTitle = jobTitle;
		this.startDate = startDate;
		this.endDate = endDate;
		this.positionFeatures = positionsFeatures;
		this.companyName = companyName;
		this.companyPlace = companyPlace;
		this.description = description;
	}

	@Override
	public String toString() {
		return "Experience{" +
				"jobTitle='" + jobTitle + '\'' +
				", startDate='" + startDate + '\'' +
				", endDate='" + endDate + '\'' +
				", positionFeatures=" + positionFeatures +
				", companyName='" + companyName + '\'' +
				", companyPlace='" + companyPlace + '\'' +
				", description='" + description + '\'' +
				'}';
	}
}
