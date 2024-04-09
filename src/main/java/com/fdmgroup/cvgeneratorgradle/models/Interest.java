package com.fdmgroup.cvgeneratorgradle.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class Interest {

	private int id;

	private String interestName;

	
	public Interest(String interestName) {
		this.interestName = interestName;
	}

}
