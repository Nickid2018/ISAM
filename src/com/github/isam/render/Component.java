package com.github.isam.render;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import com.github.isam.phys.*;
import com.github.isam.input.*;
import com.github.isam.render.vertex.*;
import com.github.isam.render.shader.*;
import com.github.isam.render.window.*;

public abstract class Component {

	protected Renderer renderer;
	protected AABB position;
	protected boolean focused;
	protected int zIndex;

	public Component(Renderer renderer, AABB position) {
		this.renderer = renderer;
		this.position = position;
	}

	public abstract void render();

	public abstract void onResize(int sWidth, int sHeight, int reWidth, int reHeight);
	
	public Component getComponentTouched(int xpos, int ypos) {
		return MouseEvent.checkInRange(position, xpos, ypos) ? this : null;
	}

	public boolean isFocused() {
		return focused;
	}

	public Component setFocused(boolean focused) {
		this.focused = focused;
		return this;
	}
	
	public int getZIndex() {
		return zIndex;
	}
	
	public Component setZIndex(int zIndex) {
		this.zIndex = zIndex;
		return this;
	}

	public void onFocus(boolean focus) {
	}

	public void onMouseEnter() {
		if (getCursorInComponent() != -1)
			GLFW.glfwSetCursor(renderer.getWindow().getHandle(), getCursorInComponent());
	}

	public long getCursorInComponent() {
		return -1;
	}

	public void onMouseLeave() {
		if (getCursorInComponent() != -1)
			GLFW.glfwSetCursor(renderer.getWindow().getHandle(), Cursors.DEFAULT_CURSOR);
	}

	public void onMouseEvent(MouseEvent event) {
		
	}

	public void onKeyEvent(KeyEvent event) {
	}

	public static VertexArray prepareTextureRender(float x1, float y1, float x2, float y2, float tx1, float ty1, float tx2,
			float ty2, boolean dynamic) {
		VertexBuffer buffer = new VertexBuffer(4, dynamic ? GL30.GL_STREAM_DRAW : GL30.GL_STATIC_DRAW);
		buffer.pos(x1, y1, 0).color(1, 1, 1).uv(tx1, ty2).endVertex();
		buffer.pos(x2, y1, 0).color(1, 1, 1).uv(tx2, ty2).endVertex();
		buffer.pos(x2, y2, 0).color(1, 1, 1).uv(tx2, ty1).endVertex();
		buffer.pos(x1, y2, 0).color(1, 1, 1).uv(tx1, ty1).endVertex();
		ElementBuffer ebo = new ElementBuffer(2);
		ebo.putTriangle(0, 1, 3);
		ebo.putTriangle(1, 2, 3);
		VertexArray array = new VertexArray(Shaders.SIMPLE);
		array.bindVBO(buffer);
		array.bindEBO(ebo);
		array.upload();
		return array;
	}
}
