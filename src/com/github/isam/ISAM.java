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

import java.io.FileInputStream;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import com.github.isam.crash.CrashReport;
import com.github.isam.render.*;
import com.github.isam.render.shader.Shaders;
import com.github.isam.render.texture.Image;
import com.github.isam.render.texture.Texture;
import com.github.isam.render.vertex.ElementBuffer;
import com.github.isam.render.vertex.VertexArray;
import com.github.isam.render.vertex.VertexBuffer;
import com.github.isam.render.window.*;
import com.github.isam.sound.OggAudioStream;
import com.github.isam.sound.SoundProperties;
import com.github.isam.sound.SoundSystem;
import com.github.isam.sound.StaticSound;

public class ISAM {

	private Renderer renderer;
	private Window window;

	private static ISAM instance;

	public static void main(String[] args) {
		instance = new ISAM();
		instance.initGLAndRun();
	}

	private ISAM() {
		initializeThrowableListener();
	}

	// This is a test of rendering
	private void initGLAndRun() {
		DisplayData data = new DisplayData();
		data.frameLimit = 60;
		data.fullScreen = false;
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

		window.setIcon(ISAM.class.getResourceAsStream("/assets/textures/logo/milogo16.png"),
				ISAM.class.getResourceAsStream("/assets/textures/logo/milogo32.png"));

		Thread.currentThread().setName("Render Thread");

		SoundSystem.init();

		Image texture;
		try {
			texture = Image.read(new FileInputStream("D:\\testFiles\\Popipa_12thSG.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Texture tex = new Texture(texture, 4).setLinear(true).update();

		Image texture2;
		try {
			texture2 = Image.read(new FileInputStream("D:\\testFiles\\ppp.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Texture tex2 = new Texture(texture2, 4).setLinear(true).update();

		try {
			StaticSound sound = new StaticSound(
					new OggAudioStream(new FileInputStream("D:\\testFiles\\Poppin'Party - (Kizuna Music).ogg")));
			Thread.sleep(3000);
			sound.playSound(SoundProperties.create().wihPitch(1).wihVolume(0.02f));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Test VAO
		VertexBuffer buffer = new VertexBuffer(4, GL30.GL_STREAM_DRAW);
		buffer.pos(0, 1, 0).color(1, 0, 0).uv(1, 1).endVertex();
		buffer.pos(1, 0, 0).color(0, 1, 0).uv(1, 0).endVertex();
		buffer.pos(0, -1, 0).color(0, 0, 1).uv(0, 0).endVertex();
		buffer.pos(-1, 0, 0).color(1, 1, 1).uv(0, 1).endVertex();
		ElementBuffer ebo = new ElementBuffer(2);
		ebo.putTriangle(0, 1, 3);
		ebo.putTriangle(1, 2, 3);
		VertexArray array = new VertexArray(Shaders.SIMPLE);
		array.bindVBO(buffer);
		array.bindEBO(ebo);
		array.upload();

		VertexBuffer buffer2 = new VertexBuffer(4);
		buffer2.pos(1, 1, 0).color(0, 1, 1).uv(1, 1).endVertex();
		buffer2.pos(1, -1, 0).color(1, 0, 1).uv(1, 0).endVertex();
		buffer2.pos(-1, -1, 0).color(1, 1, 0).uv(0, 0).endVertex();
		buffer2.pos(-1, 1, 0).color(1, 1, 1).uv(0, 1).endVertex();
		ElementBuffer ebo2 = new ElementBuffer(2);
		ebo2.putTriangle(0, 1, 3);
		ebo2.putTriangle(1, 2, 3);
		VertexArray array2 = new VertexArray(Shaders.SIMPLE);
		array2.bindVBO(buffer2);
		array2.bindEBO(ebo2);
		array2.upload();

		while (!window.shouldClose()) {
			window.clear();

			long time = System.currentTimeMillis() / 10L;
			buffer.updateVertexPos(0, nowX(0, 1, time), nowY(0, 1, time), 0);
			buffer.updateVertexPos(1, nowX(1, 0, time), nowY(1, 0, time), 0);
			buffer.updateVertexPos(2, nowX(0, -1, time), nowY(0, -1, time), 0);
			buffer.updateVertexPos(3, nowX(-1, 0, time), nowY(-1, 0, time), 0);

			tex2.activeAndBind(0);
			array2.render();
			tex2.unbind();

			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.5f);
			tex.activeAndBind(0);
			array.render();
			tex.unbind();
			GL11.glDisable(GL11.GL_ALPHA_TEST);

			window.updateDisplay(false);
			window.limitDisplayFPS();
		}
		window.close();
		SoundSystem.stop();
	}

	public float nowX(float x, float y, long time) {
		float sin = (float) Math.sin(Math.toRadians(time));
		float cos = (float) Math.cos(Math.toRadians(time));
		return cos * x - sin * y;
	}

	public float nowY(float x, float y, long time) {
		float sin = (float) Math.sin(Math.toRadians(time));
		float cos = (float) Math.cos(Math.toRadians(time));
		return sin * x + cos * y;
	}

	public static ISAM getInstance() {
		return instance;
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public static void initializeThrowableListener() {
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
			CrashReport report = new CrashReport("Final throwable tracker", e);
			report.writeToFile("plain");
			System.exit(-1);
		});
	}
}
