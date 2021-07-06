package com.github.isam.crash;

public class DetectedCrashException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1430051357808450887L;

	private final CrashReport report;

	public DetectedCrashException(CrashReport report) {
		this.report = report;
	}

	public CrashReport getReport() {
		return report;
	}
}
