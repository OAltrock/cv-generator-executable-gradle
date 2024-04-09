package com.fdmgroup.cvgeneratorgradle.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Stream {

	private int id;

	private String streamName;

	private List<String> presetTraining;

	private List<String> presetCompetences;
}
