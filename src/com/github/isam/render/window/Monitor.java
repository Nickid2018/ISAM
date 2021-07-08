package com.github.isam.render.window;

import java.util.*;
import org.lwjgl.glfw.*;
import com.google.common.collect.*;

public final class Monitor {

	private final long monitor;

	private final List<VideoMode> videoModes;

	private VideoMode currentMode;

	private int x;

	private int y;

	public Monitor(long l) {
		monitor = l;
		videoModes = Lists.newArrayList();
		refreshVideoModes();
	}

	private void refreshVideoModes() {
		this.videoModes.clear();
		GLFWVidMode.Buffer buffer = GLFW.glfwGetVideoModes(monitor);
		for (int i = buffer.limit() - 1; i >= 0; i--) {
			buffer.position(i);
			VideoMode videoMode = new VideoMode(buffer);
			if (videoMode.getRedBits() >= 8 && videoMode.getGreenBits() >= 8 && videoMode.getBlueBits() >= 8)
				videoModes.add(videoMode);
		}
		int[] arrayInt = new int[1];
		int[] arrayInt1 = new int[1];
		GLFW.glfwGetMonitorPos(this.monitor, arrayInt, arrayInt1);
		x = arrayInt[0];
		y = arrayInt1[0];
		GLFWVidMode gLfWvidMode = GLFW.glfwGetVideoMode(monitor);
		currentMode = new VideoMode(gLfWvidMode);
	}

	public VideoMode getPreferredVidMode(Optional<VideoMode> optional) {
		if (optional.isPresent()) {
			VideoMode videoMode = optional.get();
			for (VideoMode videoMode1 : this.videoModes) {
				if (videoMode1.equals(videoMode))
					return videoMode1;
			}
		}
		return getCurrentMode();
	}

	public int getVideoModeIndex(VideoMode videoMode) {
		return this.videoModes.indexOf(videoMode);
	}

	public VideoMode getCurrentMode() {
		return this.currentMode;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public VideoMode getMode(int i) {
		return videoModes.get(i);
	}

	public int getModeCount() {
		return videoModes.size();
	}

	public long getMonitor() {
		return monitor;
	}

	public String toString() {
		return String.format("Monitor[%s %sx%s %s]", monitor, x, y, currentMode);
	}
}