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

import com.github.isam.render.*;
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

	private void initGLAndRun() {
		DisplayData data = new DisplayData();
		data.frameLimit = 60;
		data.fullScreen = false;
		data.vsync = true;
		data.width = 800;
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
		while (!window.shouldClose()) {
			window.limitDisplayFPS();
		}
	}

	public static ISAM getInstance() {
		return instance;
	}

	public Renderer getRenderer() {
		return renderer;
	}
}
