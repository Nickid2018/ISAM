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

import com.google.common.base.*;

public class Version {

	public final int major;
	public final int minor;
	public final int revision;
	public final VersionType type;

	public Version(int major, int minor, int revision) {
		this(major, minor, revision, VersionType.STABLE);
	}

	public Version(int major, int minor, int revision, VersionType type) {
		Preconditions.checkArgument(major < 100 && minor < 100 && revision < 10000, "invalid version");
		Preconditions.checkArgument(type != null, "invalid type");
		this.major = major;
		this.minor = minor;
		this.revision = revision;
		this.type = type;
	}

	public boolean isMoreThan(Version ver) {
		return isMoreThan(ver, false);
	}

	public boolean isMoreThan(Version ver, boolean equal) {
		return quickParse() > ver.quickParse() || (equal && equals(ver, true));
	}

	public boolean isLessThan(Version ver) {
		return isLessThan(ver, false);
	}

	public boolean isLessThan(Version ver, boolean equal) {
		return quickParse() < ver.quickParse() || (equal && equals(ver, true));
	}

	private int quickParse() {
		return major * 1000000 + minor * 10000 + revision;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Version) && equals((Version) obj, false);
	}

	public boolean equals(Version ver, boolean ignoretype) {
		return major == ver.major && minor == ver.minor && revision == ver.revision
				&& (ignoretype || this.type == ver.type);
	}

	public String toString() {
		return major + "." + minor + "." + revision + " " + type.getName();
	}

	public static Version fromString(String ver) {
		ver = ver.trim();
		String[] splitted = ver.split(" ");
		VersionType type = getType(splitted);
		String[] version = splitted[0].split("\\.");
		int major = getOrZero(version, 0);
		int minor = getOrZero(version, 1);
		int revision = getOrZero(version, 2);
		return new Version(major, minor, revision, type);
	}

	private static VersionType getType(String[] splitted) {
		return splitted.length == 2 ? VersionType.fromString(splitted[1]) : VersionType.STABLE;
	}

	private static int getOrZero(String[] version, int index) {
		return version.length > index ? Integer.parseInt(version[index]) : 0;
	}
}
