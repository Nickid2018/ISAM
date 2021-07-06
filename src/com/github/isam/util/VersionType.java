package com.github.isam.util;

public enum VersionType {

	IN_DEVELOP("indev"), ALPHA("alpha"), BETA("beta"), TRIAL("trial"), STABLE("stable");

	private final String name;

	private VersionType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static VersionType fromString(String type) {
		switch (type.toLowerCase()) {
		case "indev":
			return IN_DEVELOP;
		case "alpha":
			return ALPHA;
		case "beta":
			return BETA;
		case "trial":
			return TRIAL;
		}
		return STABLE;
	}
}
