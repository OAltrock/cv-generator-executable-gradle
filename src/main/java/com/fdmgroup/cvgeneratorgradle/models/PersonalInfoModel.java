package com.fdmgroup.cvgeneratorgradle.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PersonalInfoModel {
    private String firstName;
    private String lastName;
    private String email;
    private String location;
    private String stream;

    public PersonalInfoModel(String firstName, String lastName, String email, String location, String stream) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.location = location;
        this.stream = stream;
    }
}
