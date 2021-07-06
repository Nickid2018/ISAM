package com.github.isam.render;

public interface Renderer {

	public void drawTexture(String resourceName, float x, float y);

	public void drawText(CharSequence str, float x, float y);
}
