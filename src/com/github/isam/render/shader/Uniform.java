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

import java.nio.*;
import org.lwjgl.system.*;
import javax.annotation.*;
import com.google.common.base.*;

import static org.lwjgl.opengl.GL30.*;

public class Uniform {

	public enum Type {
		INT_1(1), INT_2(2), INT_3(3), INT_4(4), // int buffer
		FLOAT_1(1), FLOAT_2(2), FLOAT_3(3), FLOAT_4(4), // float buffer
		MATRIX_2(4), MATRIX_3(9), MATRIX_4(16); // Matrix

		public int count;

		private Type(int count) {
			this.count = count;
		}
	}

	private final Type type;
	private final String name;
	private int location;

	@Nullable
	private IntBuffer ibuf;
	@Nullable
	private FloatBuffer fbuf;

	private boolean dirty;

	public Uniform(String name, Type type) {
		Preconditions.checkNotNull(name);
		Preconditions.checkNotNull(type);
		this.type = type;
		this.name = name;
		if (type.ordinal() < 4)
			ibuf = MemoryUtil.memAllocInt(type.count);
		else
			fbuf = MemoryUtil.memAllocFloat(type.count);
		markDirty();
	}

	protected void updateUniformLocation(int program) {
		location = glGetUniformLocation(program, name);
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public int getLocation() {
		return location;
	}

	private void markDirty() {
		dirty = true;
	}

	public void setInt(int... data) {
		Preconditions.checkArgument(data.length != type.count);
		Preconditions.checkState(ibuf != null);
		ibuf.position(0);
		ibuf.put(data);
		markDirty();
	}

	public void setFloat(float... data) {
		Preconditions.checkArgument(data.length != type.count);
		Preconditions.checkState(fbuf != null);
		fbuf.position(0);
		fbuf.put(data);
		markDirty();
	}

	public void upload() {
		if (!dirty)
			return;
		dirty = false;
		if (type.ordinal() < 4) {
			ibuf.clear();
			switch (type) {
			case INT_1:
				glUniform1iv(location, ibuf);
				break;
			case INT_2:
				glUniform2iv(location, ibuf);
				break;
			case INT_3:
				glUniform3iv(location, ibuf);
				break;
			case INT_4:
				glUniform4iv(location, ibuf);
				break;
			default:
			}
		} else {
			fbuf.clear();
			switch (type) {
			case FLOAT_1:
				glUniform1fv(location, fbuf);
				break;
			case FLOAT_2:
				glUniform2fv(location, fbuf);
				break;
			case FLOAT_3:
				glUniform3fv(location, fbuf);
				break;
			case FLOAT_4:
				glUniform4fv(location, fbuf);
				break;
			case MATRIX_2:
				glUniformMatrix2fv(location, false, fbuf);
				break;
			case MATRIX_3:
				glUniformMatrix3fv(location, false, fbuf);
				break;
			case MATRIX_4:
				glUniformMatrix4fv(location, false, fbuf);
				break;
			default:
			}
		}
	}
}
