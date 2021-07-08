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

import static org.lwjgl.opengl.GL30.*;

public class ElementBuffer {

	private int id;
	private int mode;
	private IntBuffer indices;
	private int triangles;

	public ElementBuffer(int size) {
		this(size, GL_STATIC_DRAW);
	}

	public ElementBuffer(int size, int mode) {
		this.mode = mode;
		id = glGenBuffers();
		indices = ByteBuffer.allocateDirect(size * 3 * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
	}

	private void ensureSize(int add) {
		if (indices.capacity() < indices.position() + add) {
			IntBuffer buffer = ByteBuffer.allocateDirect(indices.capacity() * 2).order(ByteOrder.nativeOrder())
					.asIntBuffer();
			int pos = indices.position();
			indices.position(0);
			buffer.put(indices);
			buffer.rewind();
			indices = buffer;
			indices.position(pos);
		}
	}

	public ElementBuffer putTriangle(int a, int b, int c) {
		ensureSize(3);
		triangles++;
		indices.put(a).put(b).put(c);
		return this;
	}
	
	public int getTriangles() {
		return triangles;
	}

	public int getId() {
		return id;
	}

	public ElementBuffer upload() {
		int pos = indices.position();
		indices.position(0);
		indices.limit(pos);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, mode);
		return this;
	}
}
