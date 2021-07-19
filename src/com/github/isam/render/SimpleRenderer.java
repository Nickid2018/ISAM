package com.github.isam.render;

import com.github.isam.input.*;
import com.github.isam.render.window.*;

public class SimpleRenderer implements Renderer, MouseInputListener {

	private Window window;
	private RootComponent component;

	public SimpleRenderer(Window window) {
		this.window = window;
		component = new RootComponent(this);
		window.setMouse(this);
	}

	@Override
	public Window getWindow() {
		return window;
	}

	@Override
	public float getXPosition(int px) {
		return 2.0f * px / window.getWidth() - 1;
	}

	@Override
	public float getYPosition(int px) {
		return 1 - 2.0f * px / window.getHeight();
	}

	@Override
	public float getHorizonLength(int px) {
		return 2.0f * px / window.getWidth();
	}

	@Override
	public float getVerticalLength(int px) {
		return 2.0f * px / window.getHeight();
	}

	public RootComponent getRoot() {
		return component;
	}

	public void render() {
		component.render();
	}

	@Override
	public void onMouse(long window, int button, int action, int mods) {

	}

	@Override
	public void onScroll(long window, double xoffset, double yoffset) {

	}

	@Override
	public void onDrop(long window, int count, long names) {

	}

	@Override
	public void onMove(long window, double xpos, double ypos) {

	}
}
