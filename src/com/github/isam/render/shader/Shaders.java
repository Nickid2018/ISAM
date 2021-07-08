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
package com.github.isam.render.shader;

import java.io.*;

public class Shaders {

	public static final ShaderProgram SIMPLE;

	static {
		try {
			SIMPLE = ShaderProgram.createFromJAR("/assets/shader/simple.vsh", "/assets/shader/simple.fsh",
					new Uniform("sampler", Uniform.Type.INT_1));
		} catch (IOException e) {
			throw new RuntimeException("Can't create shader", e);
		}
		SIMPLE.getUniform("sampler").setInt(0);
	}
}
