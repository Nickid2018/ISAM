package com.github.isam.render.window;

import org.lwjgl.glfw.*;

public class Cursors {

	public static long DEFAULT_CURSOR;

	public static void init() {
		DEFAULT_CURSOR = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
	}
}
