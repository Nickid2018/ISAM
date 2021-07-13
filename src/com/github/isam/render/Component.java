package com.github.isam.render;

import org.lwjgl.glfw.*;
import com.github.isam.input.*;
import com.github.isam.render.window.*;

public abstract class Component {

	protected Window window;
	protected int zIndex;
	protected boolean focused;

	public Component(Window window) {
		this.window = window;
	}

	public abstract void render();

	public abstract boolean checkInComponent(int xpos, int ypos);

	public abstract void onResize(int sWidth, int sHeight, int reWidth, int reHeight);

	public int getZIndex() {
		return zIndex;
	}

	public void setZIndex(int z) {
		zIndex = z;
	}

	public boolean isFocused() {
		return focused;
	}

	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	public void onFocus(boolean focus) {
	}

	public void onMouseEnter() {
		if (getCursorInComponent() != -1)
			GLFW.glfwSetCursor(window.getHandle(), getCursorInComponent());
	}

	public long getCursorInComponent() {
		return -1;
	}

	public void onMouseLeave() {
		if (getCursorInComponent() != -1)
			GLFW.glfwSetCursor(window.getHandle(), Cursors.DEFAULT_CURSOR);
	}

	public void onMouseEvent(MouseEvent event) {
	}

	public void onKeyEvent(KeyEvent event) {
	}
}
