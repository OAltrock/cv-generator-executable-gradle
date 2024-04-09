package com.fdmgroup.cvgeneratorgradle.models;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Base64;

@Getter
@NoArgsConstructor
public class ProfilePicture {
	private int id;

	private byte[] base64;

	public ProfilePicture(byte[] base64) {
		this.base64 = Base64.getDecoder().decode(base64);
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setBase64(String base64) {
		this.base64 = Base64.getDecoder().decode(base64);
	}

}
