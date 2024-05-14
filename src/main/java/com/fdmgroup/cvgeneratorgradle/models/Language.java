package com.fdmgroup.cvgeneratorgradle.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fdmgroup.cvgeneratorgradle.models.enums.LanguageLevel;

@Getter
@Setter
@NoArgsConstructor
public class Language {

	private int id;

	private String languageType;

	private LanguageLevel languageLevel;
	

	public Language(String languageType, LanguageLevel languageLevel) {
		this.languageType = languageType;
		this.languageLevel = languageLevel;
	}

	@Override
	public String toString() {
		return "Language{" +
				"languageType='" + languageType + '\'' +
				", languageLevel=" + languageLevel +
				'}';
	}
}
