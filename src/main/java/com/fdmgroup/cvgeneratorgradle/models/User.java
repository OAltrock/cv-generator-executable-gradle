package com.fdmgroup.cvgeneratorgradle.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class User {
	
	private int id;

	private String firstName;

	private String lastName;

	private String email;

	/*private String password;*/

	private ProfilePicture profilePicture;

	private String role;

	private List<CVTemplate> cvs;



	public User(String firstName, String lastName, String email, String role) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.role = role;
	}

	public User(String firstName, String lastName, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}

	@Override
	public String toString() {
		return firstName + " " + lastName + " - " + role;
	}
	
}
