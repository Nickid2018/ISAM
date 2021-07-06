package com.github.isam.crash;

import java.util.*;
import java.util.function.*;
import com.github.isam.*;
import com.google.common.collect.*;

public class CrashReportSession {

	private String name;
	private Thread thread;
	private Map<String, Supplier<String>> details = Maps.newLinkedHashMap();

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
