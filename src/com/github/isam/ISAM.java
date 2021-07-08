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
package com.github.isam;

import java.io.IOException;

import com.github.isam.render.*;
import com.github.isam.render.shader.Shaders;
import com.github.isam.render.texture.Image;
import com.github.isam.render.texture.Texture;
import com.github.isam.render.vertex.ElementBuffer;
import com.github.isam.render.vertex.VertexArray;
import com.github.isam.render.vertex.VertexBuffer;
import com.github.isam.render.window.*;

public class ISAM {

	private Renderer renderer;
	private Window window;

	private static ISAM instance;

	public static void main(String[] args) {
		instance = new ISAM();
		instance.initGLAndRun();
	}

	private ISAM() {

	}

	// This is a test of rendering
	private void initGLAndRun() {
		DisplayData data = new DisplayData();
		data.frameLimit = 60;
		data.fullScreen = true;
		data.vsync = true;
		data.width = 650;
		data.height = 600;
		window = new Window("ISAM", data, new WindowEventListener() {

			@Override
			public void onResizeDisplay() {
				System.out.println("resize!");
			}

			@Override
			public void onFocus(boolean focus) {
				System.out.println("focus update! " + focus);
			}
		});
		Thread.currentThread().setName("Render Thread");
		Image texture;
		try {
			texture = Image.read(ISAM.class.getResourceAsStream("/assets/textures/ksm128128.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Texture tex = new Texture(texture, 0).setLinear(true).update();
		// Test VAO
		VertexBuffer buffer = new VertexBuffer(4);
		buffer.pos(0.5f, 0.5f, 0).color(1, 0, 0).uv(1, 1).endVertex();
		buffer.pos(0.5f, -0.5f, 0).color(0, 1, 0).uv(1, 0).endVertex();
		buffer.pos(-0.5f, -0.5f, 0).color(0, 0, 1).uv(0, 0).endVertex();
		buffer.pos(-0.5f, 0.5f, 0).color(1, 1, 1).uv(0, 1).endVertex();
		ElementBuffer ebo = new ElementBuffer(2);
		ebo.putTriangle(0, 1, 3);
		ebo.putTriangle(1, 2, 3);
		VertexArray array = new VertexArray(Shaders.SIMPLE);
		array.bindVBO(buffer);
		array.bindEBO(ebo);
		array.upload();
		while (!window.shouldClose()) {
			window.clear();
			tex.activeAndBind(0);
			array.render();
			window.updateDisplay(false);
			window.limitDisplayFPS();
		}
		window.close();
	}

	public static ISAM getInstance() {
		return instance;
	}

	public Renderer getRenderer() {
		return renderer;
	}
}
