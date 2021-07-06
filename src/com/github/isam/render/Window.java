package com.github.isam.render;

import java.util.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import javax.annotation.*;
import org.apache.log4j.*;

public class Window {

	private long handle;
	@Nullable
	private Monitor monitor;

	private int width;
	private int height;
	private int windowWidth;
	private int windowHeight;
	private int posX;
	private int posY;
	private int x;
	private int y;
	private boolean fullScreen;
	private Optional<VideoMode> preferredFullscreenVideoMode;

	private static final Logger LOGGER = Logger.getLogger("Window");

	public Window(String title, DisplayData data) {
		setBootGlErrorCallback();
		monitor = GLFW.glfwGetPrimaryMonitor() != 0 ? new Monitor(GLFW.glfwGetPrimaryMonitor()) : null;
		windowWidth = width = (data.width > 0) ? data.width : 1;
		windowHeight = height = (data.height > 0) ? data.height : 1;
		GLFW.glfwDefaultWindowHints();
		handle = GLFW.glfwCreateWindow(width, height, title, data.fullScreen ? monitor.getMonitor() : 0, 0);
		if (monitor != null) {
			posX = x = monitor.getX() - width / 2;
			posY = y = monitor.getY() - height / 2;
		} else {
			int[] xia = new int[1];
			int[] yia = new int[1];
			GLFW.glfwGetWindowPos(handle, xia, yia);
			posX = x = xia[0];
			posY = y = yia[0];
		}
		GLFW.glfwMakeContextCurrent(handle);
		GL.createCapabilities();
		setMode();
		GLFW.glfwSetWindowPosCallback(handle, this::onMove);
		GLFW.glfwSetWindowSizeCallback(handle, this::onResize);
	}

	private void setBootGlErrorCallback() {
		GLFW.glfwSetErrorCallback(Window::bootCrash);
	}

	private static void bootCrash(int i, long l) {
		throw new IllegalStateException("GLFW error " + i + ": " + MemoryUtil.memUTF8(l));
	}

	public void onMove(long handle, int moveX, int moveY) {
		x = moveX;
		y = moveY;
	}

	public void onResize(long handle, int reWidth, int reHeight) {
		width = reWidth;
		height = reHeight;
	}

	private void setMode() {
		if (fullScreen) {
			if (monitor == null) {
				LOGGER.warn("Failed to find suitable monitor for fullscreen mode");
				fullScreen = false;
			} else {
				VideoMode videoMode = monitor.getPreferredVidMode(this.preferredFullscreenVideoMode);
				if (GLFW.glfwGetWindowMonitor(handle) == 0L) {
					posX = x;
					posY = y;
					windowWidth = width;
					windowHeight = height;
				}
				x = 0;
				y = 0;
				width = videoMode.getWidth();
				height = videoMode.getHeight();
				GLFW.glfwSetWindowMonitor(handle, monitor.getMonitor(), x, y, width, height,
						videoMode.getRefreshRate());
			}
		} else {
			x = posX;
			y = posY;
			width = windowWidth;
			height = windowHeight;
			GLFW.glfwSetWindowMonitor(handle, 0L, x, y, width, height, -1);
		}
	}
}
