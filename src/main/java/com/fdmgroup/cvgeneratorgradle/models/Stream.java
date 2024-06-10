package com.fdmgroup.cvgeneratorgradle.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Stream {

	private String streamName;

	private String startDate;

	private String endDate;

	private List<String> presetTraining;

	private Set<String> presetCompetences;
}
