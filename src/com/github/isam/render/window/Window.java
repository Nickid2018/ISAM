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
package com.github.isam.render.window;

import java.io.*;
import java.nio.*;
import java.util.*;
import org.lwjgl.stb.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import javax.annotation.*;
import com.github.isam.input.*;
import org.apache.logging.log4j.*;
import com.github.isam.render.texture.*;

public class Window implements AutoCloseable {

	public static final Logger LOGGER = LogManager.getLogger("Window");

	// Strong reference
	private static final GLFWErrorCallback ERR_CALLBACK = GLFWErrorCallback
			.create((code, str) -> LOGGER.error("GL Error! [{}] {}", code, MemoryUtil.memUTF8(str)));

	private long handle;
	@Nullable
	private Monitor monitor;
	private WindowEventListener window;
	private KeyboardInputListener keyboard;
	private MouseInputListener mouse;

	private int width;
	private int height;
	private int windowWidth;
	private int windowHeight;
	private int posX;
	private int posY;
	private int x;
	private int y;

	private boolean vsync;
	private boolean fullScreen;
	private boolean actuallyFullscreen;

	private int frameLimit;

	private double lastDrawTime;

	public Window(String title, DisplayData data, WindowEventListener window) {
		this.window = window;
		frameLimit = data.frameLimit;
		setBootGlErrorCallback();
		GLFW.glfwInit();
		Cursors.init();
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
		GLFW.glfwSetErrorCallback(ERR_CALLBACK).free();
	}

	private void setBootGlErrorCallback() {
		GLFW.glfwSetErrorCallback(Window::bootCrash);
	}

	private static void bootCrash(int i, long l) {
		throw new IllegalStateException("GLFW error " + i + ": " + MemoryUtil.memUTF8(l));
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public long getHandle() {
		return handle;
	}

	public boolean shouldClose() {
		return GLFW.glfwWindowShouldClose(handle);
	}

	public void onMove(long handle, int moveX, int moveY) {
		x = moveX;
		y = moveY;
	}

	public void onResize(long handle, int reWidth, int reHeight) {
		int sWidth = width;
		int sHeight = height;
		width = reWidth;
		height = reHeight;
		GL11.glViewport(0, 0, width, height);
		window.onResizeDisplay(sWidth, sHeight, reWidth, reHeight);
	}

	public void onFocus(long handle, boolean focus) {
		if (handle == this.handle)
			window.onFocus(focus);
	}

	public KeyboardInputListener getKeyboard() {
		return keyboard;
	}

	public void setKeyboard(KeyboardInputListener keyboard) {
		this.keyboard = keyboard;
		GLFW.glfwSetKeyCallback(handle, keyboard::onKeyInput);
		GLFW.glfwSetCharCallback(handle, keyboard::onCharInput);
		GLFW.glfwSetCharModsCallback(handle, keyboard::onCharModInput);
	}

	public MouseInputListener getMouse() {
		return mouse;
	}

	public void setMouse(MouseInputListener mouse) {
		this.mouse = mouse;
		GLFW.glfwSetMouseButtonCallback(handle, mouse::onMouse);
		GLFW.glfwSetCursorPosCallback(handle, mouse::onMove);
		GLFW.glfwSetScrollCallback(handle, mouse::onScroll);
		GLFW.glfwSetDropCallback(handle, mouse::onDrop);
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
			int sWidth = width;
			int sHeight = height;
			setMode();
			window.onResizeDisplay(sWidth, sHeight, width, height);
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

	public void clear() {
		GL11.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
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
