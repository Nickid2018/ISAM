/*
 * Copyright 2021 ISAM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
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
 */
package com.github.isam;

import com.github.isam.util.Version;
import com.github.isam.util.VersionType;

import java.util.Random;

public class Constants {

    public static final Version VERSION = new Version(1, 0, 0, VersionType.IN_DEVELOP);
    public static final String VERSION_IN_STRING = VERSION.toString();

    public static final String lineSeparator = System.getProperty("line.separator");

    public static final Random RANDOM = new Random();
}
