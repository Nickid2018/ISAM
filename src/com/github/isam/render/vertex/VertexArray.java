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
 */
package com.github.isam.render.vertex;

import com.github.isam.render.shader.*;

import static org.lwjgl.opengl.GL30.*;

public class VertexArray {

	private ElementBuffer ebo;
	private VertexBuffer vbo;
	private ShaderProgram shader;

	private int id;

	public VertexArray(ShaderProgram shader) {
		this.shader = shader;
		id = glGenVertexArrays();
	}

	public VertexArray bindVBO(VertexBuffer vbo) {
		this.vbo = vbo;
		return this;
	}

	public VertexArray bindEBO(ElementBuffer ebo) {
		this.ebo = ebo;
		return this;
	}

	public void upload() {
		glBindVertexArray(id);
		ebo.upload();
		vbo.upload();
		glBindVertexArray(0);
	}

	public void render() {
		shader.use();
		glBindVertexArray(id);
		if (ebo == null)
			glDrawArrays(GL_TRIANGLES, 0, vbo.getVertexes());
		else
			glDrawElements(GL_TRIANGLES, ebo.getTriangles(), GL_UNSIGNED_INT, 0);
	}
}
