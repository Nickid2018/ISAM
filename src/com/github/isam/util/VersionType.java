/*
 * Copyright 2021 ISAM
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 */
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
