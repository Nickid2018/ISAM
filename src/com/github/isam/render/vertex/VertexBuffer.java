package com.github.isam.render.vertex;

import java.nio.*;

import static org.lwjgl.opengl.GL30.*;

public class VertexBuffer {

	private int id;
	private int mode;
	private FloatBuffer vertices;
	private int nowVertexes = 0;

	public VertexBuffer(int size, int vertexes) {
		this(size, vertexes, GL_STATIC_DRAW);
	}

	public VertexBuffer(int size, int vertexes, int mode) {
		this.mode = mode;
		id = glGenBuffers();
		vertices = ByteBuffer.allocateDirect(size * vertexes * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
	}

	private void ensureSize(int add) {
		if (vertices.capacity() < vertices.position() + add) {
			FloatBuffer buffer = ByteBuffer.allocateDirect(vertices.capacity() * 2).order(ByteOrder.nativeOrder())
					.asFloatBuffer();
			int pos = vertices.position();
			vertices.position(0);
			buffer.put(vertices);
			buffer.rewind();
			vertices = buffer;
			vertices.position(pos);
		}
	}

	public VertexBuffer pos(float x, float y, float z) {
		ensureSize(3);
		vertices.put(x).put(y).put(z);
		return this;
	}

	public VertexBuffer color(float r, float g, float b) {
		ensureSize(3);
		vertices.put(r).put(g).put(b);
		return this;
	}

	public VertexBuffer uv(float u, float v) {
		ensureSize(2);
		vertices.put(u).put(v);
		return this;
	}
	
	public void endVertex() {
		nowVertexes++;
	}
	
	public int getVertexes() {
		return nowVertexes;
	}

	public VertexBuffer upload() {
		int pos = vertices.position();
		vertices.position(0);
		vertices.limit(pos);
		glBindBuffer(GL_ARRAY_BUFFER, id);
		glBufferData(GL_ARRAY_BUFFER, vertices, mode);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 4 * 8, 0);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 4 * 8, 4 * 3);
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 4 * 8, 4 * 6);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		return this;
	}
}