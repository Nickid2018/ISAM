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
package com.github.isam.render.texture;

import static org.lwjgl.opengl.GL30.*;

public class Texture {

	private int id;
	private Image image;
	private int level;
	private boolean linear;
	private boolean clamp;

	public Texture(Image image, int level) {
		id = TextureUtil.generateTextureId();
		this.image = image;
		this.level = level;
		TextureUtil.prepareImage(id, image.getWidth(), image.getHeight());
		if (level > 0)
			glGenerateMipmap(level);
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}

	public void activeAndBind(int unit) {
		glActiveTexture(GL_TEXTURE0 + unit);
		bind();
	}

	public int getLevel() {
		return level;
	}

	public boolean isLinear() {
		return linear;
	}

	public Texture setLinear(boolean linear) {
		this.linear = linear;
		return this;
	}

	public boolean isClamp() {
		return clamp;
	}

	public Texture setClamp(boolean clamp) {
		this.clamp = clamp;
		return this;
	}

	public int getId() {
		return id;
	}

	public Image getImage() {
		return image;
	}

	public Texture update() {
		return update(0, 0, image.getWidth(), image.getHeight());
	}

	public Texture update(int x, int y, int sizeX, int sizeY) {
		bind();
		image.upload(level, x, y, 0, 0, sizeX, sizeY, linear, clamp, level > 0);
		return this;
	}
}
