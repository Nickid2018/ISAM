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
package com.github.isam.crash;

import com.github.isam.Constants;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.function.Supplier;

public class CrashReportSession {

    private final String name;
    private final Thread thread;
    private final Map<String, Supplier<String>> details = Maps.newLinkedHashMap();

    public CrashReportSession(String name) {
        this.name = name;
        thread = Thread.currentThread();
    }

    public CrashReportSession addDetail(String name, Supplier<String> info) {
        details.put(name, info);
        return this;
    }

    public CrashReportSession addDetail(String name, Object obj) {
        return addDetail(name, () -> {
            if (obj == null)
                return "[null]";
            if (obj instanceof Throwable)
                return "[ERROR]" + obj.getClass() + ":" + ((Throwable) obj).getLocalizedMessage();
            return obj.toString();
        });
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("  --- " + name + " ---");
        sb.append(Constants.lineSeparator);
        sb.append("\tThread: ");
        sb.append(thread.getName());
        sb.append(Constants.lineSeparator);
        for (Map.Entry<String, Supplier<String>> entry : details.entrySet()) {
            sb.append("\t");
            sb.append(entry.getKey());
            sb.append(": ");
            sb.append(entry.getValue().get());
            sb.append(Constants.lineSeparator);
        }
        return sb.toString();
    }
}
