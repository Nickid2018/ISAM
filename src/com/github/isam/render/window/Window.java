package com.github.isam.render.window;

import java.io.*;
import java.nio.*;
import java.util.*;
import org.lwjgl.stb.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import com.github.isam.render.texture.TextureUtil;

import javax.annotation.*;
import org.apache.log4j.*;

public class Window implements AutoCloseable {

	public static final Logger LOGGER = Logger.getLogger("Window");

	// Strong reference
	private static final GLFWErrorCallback ERR_CALLBACK = GLFWErrorCallback
			.create((code, str) -> LOGGER.error("GL Error! [" + code + "] " + MemoryUtil.memUTF8(str)));

	private long handle;
	@Nullable
	private Monitor monitor;
	private WindowEventListener handler;

	private int width;
	private int height;
	private int windowWidth;
	private int windowHeight;
	private int framebufferWidth;
	private int framebufferHeight;
	private int posX;
	private int posY;
	private int x;
	private int y;

	private boolean vsync;
	private boolean fullScreen;
	private boolean actuallyFullscreen;

	private int frameLimit;

	private double lastDrawTime;

	public Window(String title, DisplayData data, WindowEventListener handler) {
		this.handler = handler;
		frameLimit = data.frameLimit;
		setBootGlErrorCallback();
		GLFW.glfwInit();
		monitor = GLFW.glfwGetPrimaryMonitor() != 0 ? new Monitor(GLFW.glfwGetPrimaryMonitor()) : null;
		windowWidth = width = data.width > 0 ? data.width : 1;
		windowHeight = height = data.height > 0 ? data.height : 1;
		fullScreen = actuallyFullscreen = data.fullScreen;
		GLFW.glfwDefaultWindowHints();
		handle = GLFW.glfwCreateWindow(width, height, title, data.fullScreen ? monitor.getMonitor() : 0, 0);
		if (monitor != null) {
			VideoMode videoMode = monitor.getPreferredVidMode(Optional.empty());
			posX = x = monitor.getX() + videoMode.getWidth() / 2 - width / 2;
			posY = y = monitor.getY() + videoMode.getHeight() / 2 - height / 2;
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
		GLFW.glfwSetWindowFocusCallback(handle, this::onFocus);
		GLFW.glfwSetFramebufferSizeCallback(handle, this::onFramebufferResize);
		GLFW.glfwSetErrorCallback(ERR_CALLBACK).free();
	}

	private void setBootGlErrorCallback() {
		GLFW.glfwSetErrorCallback(Window::bootCrash);
	}

	private static void bootCrash(int i, long l) {
		throw new IllegalStateException("GLFW error " + i + ": " + MemoryUtil.memUTF8(l));
	}

	public int getWidth() {
		return framebufferWidth;
	}

	public int getHeight() {
		return framebufferHeight;
	}
	
	public boolean shouldClose() {
		return GLFW.glfwWindowShouldClose(handle);
	}

	public void onMove(long handle, int moveX, int moveY) {
		x = moveX;
		y = moveY;
	}

	public void onResize(long handle, int reWidth, int reHeight) {
		width = reWidth;
		height = reHeight;
	}

	public void onFocus(long handle, boolean focus) {
		if (handle == this.handle)
			handler.onFocus(focus);
	}

	public void onFramebufferResize(long handle, int reWidth, int reHeight) {
		if (handle != this.handle)
			return;
		int sWidth = framebufferWidth;
		int sHeight = framebufferHeight;
		if (reWidth == 0 || reHeight == 0)
			return;
		framebufferWidth = reWidth;
		framebufferHeight = reHeight;
		if (framebufferWidth != sWidth || framebufferHeight != sHeight)
			handler.onResizeDisplay();
	}

	public void updateVsync(boolean bool) {
		this.vsync = bool;
		GLFW.glfwSwapInterval(bool ? 1 : 0);
	}

	public void updateDisplay(boolean bool) {
		GLFW.glfwSwapBuffers(handle);
		GLFW.glfwPollEvents();
		if (fullScreen != actuallyFullscreen) {
			actuallyFullscreen = fullScreen;
			updateFullscreen(vsync);
		}
	}

	private void updateFullscreen(boolean vsync) {
		try {
			setMode();
			handler.onResizeDisplay();
			updateVsync(vsync);
		} catch (Exception exception) {
			LOGGER.error("Couldn't toggle fullscreen", exception);
		}
	}

	public void setFrameLimit(int fps) {
		frameLimit = fps;
	}

	public int getFrameLimit() {
		return frameLimit;
	}
	
	public void limitDisplayFPS() {
		double frameLength = lastDrawTime + 1.0D / frameLimit;
		double now;
		for (now = GLFW.glfwGetTime(); now < frameLength; now = GLFW.glfwGetTime())
			GLFW.glfwWaitEventsTimeout(frameLength - now);
		lastDrawTime = now;
	}

	public void close() {
		Callbacks.glfwFreeCallbacks(handle);
		ERR_CALLBACK.close();
		GLFW.glfwDestroyWindow(handle);
		GLFW.glfwTerminate();
	}

	public void setIcon(InputStream icon16, InputStream icon32) {
		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			if (icon16 == null)
				throw new FileNotFoundException("icon(16x16)");
			if (icon32 == null)
				throw new FileNotFoundException("icon(32x32)");
			IntBuffer width = memoryStack.mallocInt(1);
			IntBuffer height = memoryStack.mallocInt(1);
			IntBuffer channel = memoryStack.mallocInt(1);
			GLFWImage.Buffer buffer = GLFWImage.mallocStack(2, memoryStack);
			ByteBuffer icon16buffer = readIconPixels(icon16, width, height, channel);
			if (icon16buffer == null)
				throw new IllegalStateException("Could not load icon(16x16): " + STBImage.stbi_failure_reason());
			buffer.position(0);
			buffer.width(width.get(0));
			buffer.height(height.get(0));
			buffer.pixels(icon16buffer);
			ByteBuffer icon32buffer = readIconPixels(icon32, width, height, channel);
			if (icon32buffer == null)
				throw new IllegalStateException("Could not load icon(32x32): " + STBImage.stbi_failure_reason());
			buffer.position(1);
			buffer.width(width.get(0));
			buffer.height(height.get(0));
			buffer.pixels(icon32buffer);
			buffer.position(0);
			GLFW.glfwSetWindowIcon(handle, buffer);
			STBImage.stbi_image_free(icon16buffer);
			STBImage.stbi_image_free(icon32buffer);
		} catch (IOException var21) {
			LOGGER.error("Couldn't set icon", var21);
		}
	}

	private void setMode() {
		if (fullScreen) {
			if (monitor == null) {
				LOGGER.warn("Failed to find suitable monitor for fullscreen mode");
				fullScreen = false;
			} else {
				VideoMode videoMode = monitor.getPreferredVidMode(Optional.empty());
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

	@Nullable
	private ByteBuffer readIconPixels(InputStream in, IntBuffer sizeX, IntBuffer sizeY, IntBuffer channel)
			throws IOException {
		ByteBuffer buffer = null;
		try {
			buffer = TextureUtil.readResource(in);
			buffer.rewind();
			return STBImage.stbi_load_from_memory(buffer, sizeX, sizeY, channel, 0);
		} finally {
			if (buffer != null)
				MemoryUtil.memFree(buffer);
		}
	}
}
