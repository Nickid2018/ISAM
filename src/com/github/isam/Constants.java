package com.github.isam;

import java.util.*;
import com.github.isam.util.*;

public class Constants {

	public static final Version VERSION = new Version(1, 0, 0, VersionType.IN_DEVELOP);
	public static final String VERSION_IN_STRING = VERSION.toString();

	public static final String lineSeparator = System.getProperty("line.separator");

	public static final Random RANDOM = new Random();
}
