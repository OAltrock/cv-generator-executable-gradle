package com.fdmgroup.cvgeneratorgradle.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location {
	private int id;
	private String locationName;

	private int minExperience;

	private int maxExperience;

	private int minEducation;

	private int maxEducation;

	private int minPositionFeature;

	private int maxPositionFeature;

	private int minKeyModule;

	private int maxKeyModule;

	private int minCompetence;

	private int maxCompetence;

	private int minCertificate;

	private int maxCertificate;

	private int minLanguage;

	private int maxLanguage;

	private int minInterest;

	private int maxInterest;

	private boolean isPictureMandatory;
}
