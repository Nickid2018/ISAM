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
package com.github.isam.util;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;

public class VersionUtils {

    public static boolean isInRange(String ver, String range) {
        return isInRange(Version.fromString(ver), range);
    }

    public static boolean isInRange(Version ver, String range) {
        Preconditions.checkArgument(ver != null, "version is null!");
        Preconditions.checkArgument(range != null && !range.isEmpty(), "range is null!");
        String[] splits = split(range);
        if (splits.length > 1) {
            for (String sub : splits) {
                if (isInRange(ver, sub))
                    return true;
            }
            return false;
        }
        String[] vrange = splits[0].split(",");
        Preconditions.checkArgument(vrange.length < 3, "invalid range statement");
        if (vrange.length == 1)
            return Version.fromString(vrange[0]).equals(ver, true);
        String down = vrange[0];
        String up = vrange[1];
        Version vdown = Version.fromString(down.substring(1));
        Version vup = Version.fromString(up.substring(0, up.length() - 1));
        Preconditions.checkArgument(vdown.isLessThan(vup), "invalid range");
        Preconditions.checkArgument(down.charAt(0) == '(' || down.charAt(0) == '[', "invalid range statement");
        Preconditions.checkArgument(up.charAt(up.length() - 1) == ')' || up.charAt(up.length() - 1) == ']',
                "invalid range statement");
        return vdown.isLessThan(ver, down.charAt(0) == '[') && vup.isMoreThan(ver, up.charAt(up.length() - 1) == ']');
    }

    private static String[] split(String in) {
        ArrayList<String> al = new ArrayList<>();
        boolean isRound = false;
        int begin = 0;
        for (int i = 0; i < in.length(); i++) {
            char at = in.charAt(i);
            if (at == '(' || at == '[')
                isRound = true;
            if (at == ')' || at == ']')
                isRound = false;
            if (at == ',' && !isRound) {
                al.add(in.substring(begin, i).trim());
                begin = i + 1;
            }
        }
        if (begin != in.length())
            al.add(in.substring(begin).trim());
        // To array
        Object[] o = al.toArray();
        return Arrays.copyOf(o, o.length, String[].class);
    }
}
