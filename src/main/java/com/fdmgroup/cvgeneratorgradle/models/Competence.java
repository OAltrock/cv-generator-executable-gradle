package com.fdmgroup.cvgeneratorgradle.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class Competence {

	private int id;

	private String competenceName;
	
	
	public Competence(String competenceName) {
		this.competenceName = competenceName;
	}

}
