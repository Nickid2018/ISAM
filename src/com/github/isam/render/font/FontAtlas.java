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

package com.github.isam.render.font;

import com.github.isam.phys.AABB;
import com.github.isam.render.texture.Image;
import com.github.isam.render.texture.StaticTexture;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.lwjgl.stb.STBTTFontinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FontAtlas {

    private final Image image;
    private final Int2ObjectMap<FontVertexInfos> chars = new Int2ObjectAVLTreeMap<>();
    private final List<AABB> spareSpace = new ArrayList<>();
    private final Queue<FontVertexInfos> toUpdates = new ConcurrentLinkedQueue<>();
    private StaticTexture texture;
    private final VertexFont font;

    public FontAtlas(VertexFont font) {
        image = new Image(Image.Format.LUMINANCE, 1024, 1024, false);
        spareSpace.add(AABB.newAABB(0, 0, 1024, 1024));
        this.font = font;
    }

    public Optional<FontVertexInfos> putBitmap(STBTTFontinfo font, int codepoint, float scale, int width, int height,
                                               int xoff, int yoff, float leftSide, float advanceWidth, float topSide) {
        // Find min chunk
        AABB minChunk = null;
        for (AABB now : spareSpace) {
            if (width <= now.getWidth() && height <= now.getHeight()) {
                minChunk = now;
                break;
            }
        }
        if (minChunk == null)
            return Optional.empty();
        // Put in
        FontVertexInfos info = new FontVertexInfos();
        info.atlas = this;
        info.minU = (float) (minChunk.minX / 1024);
        info.minV = (float) (minChunk.minY / 1024);
        info.maxU = (float) ((minChunk.minX + width) / 1024);
        info.maxV = (float) ((minChunk.minY + height) / 1024);
        info.leftSide = leftSide;
        info.advanceWidth = advanceWidth;
        info.topSide = topSide;
        chars.put(codepoint, info);
        // Add to image
        image.copyFromFont(font, codepoint, width, height, scale, scale, 0, 0, (int) minChunk.minX,
                (int) minChunk.minY);
        toUpdates.offer(info);
        // Split
        spareSpace.remove(minChunk);
        double widthLen = minChunk.getWidth() - width;
        double heightLen = minChunk.getHeight() - height;
        if (widthLen == 0 && heightLen == 0)
            return Optional.of(info);
        if (widthLen == 0) {
            insertAABB(AABB.newAABB(minChunk.minX, minChunk.minY + height, minChunk.maxX, minChunk.maxY));
            return Optional.of(info);
        }
        if (heightLen == 0) {
            insertAABB(AABB.newAABB(minChunk.minX + width, minChunk.minY, minChunk.maxX, minChunk.maxY));
            return Optional.of(info);
        }
        double area1 = widthLen * height;
        double area2 = heightLen * width;
        if (area1 > area2) {
            insertAABB(AABB.newAABB(minChunk.minX + width, minChunk.minY, minChunk.maxX, minChunk.maxY));
            insertAABB(AABB.newAABB(minChunk.minX, minChunk.minY + height, minChunk.minX + width, minChunk.maxY));
        } else {
            insertAABB(AABB.newAABB(minChunk.minX, minChunk.minY + height, minChunk.maxX, minChunk.maxY));
            insertAABB(AABB.newAABB(minChunk.minX + width, minChunk.minY, minChunk.maxX, minChunk.minY + height));
        }
        return Optional.of(info);
    }

    private void insertAABB(AABB now) {
        int position = 0;
        for (; position < spareSpace.size(); position++)
            if (compareAABB(spareSpace.get(0), now))
                break;
        spareSpace.add(position, now);
    }

    private static boolean compareAABB(AABB aabb1, AABB aabb2) {
        double width1 = aabb1.getWidth();
        double width2 = aabb2.getWidth();
        double height1 = aabb1.getHeight();
        double height2 = aabb2.getHeight();
        double add1 = width1 + height1;
        double add2 = width2 + height2;
        if (add1 != add2)
            return add1 - add2 > 0;
        if (width1 != width2)
            return width1 - width2 > 0;
        if (height1 != height2)
            return height1 - height2 > 0;
        return false;
    }

    public Image getImage() {
        return image;
    }

    public FontVertexInfos getChar(int codepoint) {
        return chars.get(codepoint);
    }

    public void refresh() {
        if (texture == null)
            texture = new StaticTexture(image, 0).setLinear(font.getSize() <= 32).setClamp(true);
        while (!toUpdates.isEmpty()) {
            FontVertexInfos info = toUpdates.poll();
            int x = (int) (info.minU * 1024);
            int y = (int) (info.minV * 1024);
            int xSize = (int) ((info.maxU - info.minU) * 1024);
            int ySize = (int) ((info.maxV - info.minV) * 1024);
            texture.update(x, y, xSize, ySize);
        }
    }

    public StaticTexture getTexture() {
        refresh();
        return texture;
    }
}
