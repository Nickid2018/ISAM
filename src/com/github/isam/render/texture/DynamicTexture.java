/*
 * Copyright 2021 ISAM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
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

package com.github.isam.render.texture;

import it.unimi.dsi.fastutil.ints.IntList;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

public class DynamicTexture implements Texture {

    private final IntList delays;
    private final int totalTime;
    private final List<StaticTexture> textures = new ArrayList<>();
    private boolean linear;
    private boolean clamp;

    public DynamicTexture(DynamicImage image, int level) {
        delays = image.getDelays();
        totalTime = image.getTotalTime();
        for (int now = 0; now < image.getFrames(); now++) {
            StaticTexture texture = new StaticTexture(image.getImage(now), level);
            textures.add(texture);
        }
    }

    public DynamicTexture(List<StaticTexture> textures, IntList delays) {
        this.textures.addAll(textures);
        this.delays = delays;
        int time = 0;
        for (int now : delays)
            time += now;
        totalTime = time;
    }

    @Override
    public void bind() {
        if (totalTime != 0) {
            long remaining = System.currentTimeMillis() % totalTime;
            for (int now = 0; now < delays.size(); now++) {
                if ((remaining -= delays.getInt(now)) <= 0) {
                    textures.get(now).bind();
                    break;
                }
            }
        }
    }

    @Override
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public boolean isLinear() {
        return linear;
    }

    public DynamicTexture setLinear(boolean linear) {
        this.linear = linear;
        for (StaticTexture texture : textures)
            texture.setLinear(linear);
        return this;
    }

    public boolean isClamp() {
        return clamp;
    }

    public DynamicTexture setClamp(boolean clamp) {
        this.clamp = clamp;
        for (StaticTexture texture : textures)
            texture.setClamp(clamp);
        return this;
    }

    public DynamicTexture update() {
        for (StaticTexture texture : textures)
            texture.update();
        return this;
    }

    public DynamicTexture update(int x, int y, int sizeX, int sizeY) {
        throw new UnsupportedOperationException("update");
    }

    public StaticTexture getFrame(int frame) {
        return textures.get(frame);
    }
}
