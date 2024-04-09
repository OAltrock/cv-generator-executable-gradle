package com.fdmgroup.cvgeneratorgradle.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class Certificate {

	private int id;

	private String certificateName;
	public Certificate(String certificateName) {
		this.certificateName = certificateName;
	}

}
