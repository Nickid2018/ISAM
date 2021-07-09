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
package com.github.isam.render.vertex;

import java.nio.*;
import com.google.common.base.*;

import static org.lwjgl.opengl.GL30.*;

public class VertexBuffer {

	private int id;
	private int mode;
	private FloatBuffer vertices;
	private int nowVertexes = 0;

	public VertexBuffer(int vertextes) {
		this(vertextes, GL_STATIC_DRAW);
	}

	public VertexBuffer(int vertexes, int mode) {
		this.mode = mode;
		id = glGenBuffers();
		vertices = ByteBuffer.allocateDirect(8 * vertexes * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
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

	public VertexBuffer updateVertexPos(int vertex, float x, float y, float z) {
		Preconditions.checkArgument(vertex < nowVertexes);
		glBindBuffer(GL_ARRAY_BUFFER, id);
		glBufferSubData(GL_ARRAY_BUFFER, vertex * 8 * 4, new float[] { x, y, z });
		return this;
	}

	public VertexBuffer updateVertexColor(int vertex, float r, float g, float b) {
		Preconditions.checkArgument(vertex < nowVertexes);
		glBindBuffer(GL_ARRAY_BUFFER, id);
		glBufferSubData(GL_ARRAY_BUFFER, vertex * 8 * 4 + 4 * 3, new float[] { r, g, b });
		return this;
	}

	public VertexBuffer updateVertexUV(int vertex, float u, float v) {
		Preconditions.checkArgument(vertex < nowVertexes);
		glBindBuffer(GL_ARRAY_BUFFER, id);
		glBufferSubData(GL_ARRAY_BUFFER, vertex * 8 * 4 + 4 * 6, new float[] { u, v });
		return this;
	}

	public VertexBuffer upload() {
		int pos = vertices.position();
		vertices.position(0);
		vertices.limit(pos);
		glBindBuffer(GL_ARRAY_BUFFER, id);
		glBufferData(GL_ARRAY_BUFFER, vertices, mode);
		return this;
	}

	public VertexBuffer setPointers() {
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 4 * 8, 0);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 4 * 8, 4 * 3);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 4 * 8, 4 * 6);
		glEnableVertexAttribArray(2);
		return this;
	}
}
