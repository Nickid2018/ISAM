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
package com.github.isam.render.vertex;

import com.google.common.base.Preconditions;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL30.*;

// Default memory layout:
// | -- position -- | -- colorRGB -- | -- UV -- |
//         12               12            8       length = 32

public class VertexBuffer {

    public static final Runnable DEFAULT_POINTER_SETTER = () -> {
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 4 * 8, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 4 * 8, 4 * 3);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 4 * 8, 4 * 6);
        glEnableVertexAttribArray(2);
    };

    private final int id;
    private final int mode;
    private final Runnable pointerSetter;
    private final int size;
    private FloatBuffer vertices;
    private int nowVertexes = 0;

    public VertexBuffer(int vertexes) {
        this(vertexes, GL_STATIC_DRAW);
    }

    public VertexBuffer(int vertexes, int mode) {
        this(vertexes, mode, DEFAULT_POINTER_SETTER, 32);
    }

    public VertexBuffer(int vertexes, int mode, Runnable setter, int size) {
        this.mode = mode;
        this.size = size;
        id = glGenBuffers();
        pointerSetter = setter;
        vertices = ByteBuffer.allocateDirect(vertexes * size).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    private void ensureSize(int add) {
        if (vertices.capacity() < vertices.position() + add) {
            FloatBuffer buffer = ByteBuffer.allocateDirect(vertices.capacity() * 2).order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            int pos = vertices.position();
            vertices.position(0);
            buffer.put(vertices);
            buffer.rewind();
            vertices = buffer;
            vertices.position(pos);
        }
    }

    public VertexBuffer pos(float x, float y, float z) {
        ensureSize(3);
        vertices.put(x).put(y).put(z);
        return this;
    }

    public VertexBuffer color(int color) {
        int R = (color >> 16) & 0xFF;
        int G = (color >> 8) & 0xFF;
        int B = color & 0xFF;
        return color(R / 255f, G / 255f, B / 255f);
    }

    public VertexBuffer color(float r, float g, float b) {
        ensureSize(3);
        vertices.put(r).put(g).put(b);
        return this;
    }

    public VertexBuffer colorAlpha(int color) {
        int R = (color >> 24) & 0xFF;
        int G = (color >> 16) & 0xFF;
        int B = (color >> 8) & 0xFF;
        int A = color & 0xFF;
        return colorAlpha(R / 255f, G / 255f, B / 255f, A / 255f);
    }

    public VertexBuffer colorAlpha(float r, float g, float b, float a) {
        ensureSize(4);
        vertices.put(r).put(g).put(b).put(a);
        return this;
    }

    public VertexBuffer uv(float u, float v) {
        ensureSize(2);
        vertices.put(u).put(v);
        return this;
    }

    public void endVertex() {
        nowVertexes++;
    }

    public int getVertexes() {
        return nowVertexes;
    }

    public VertexBuffer updateVertexPos(int vertex, float x, float y, float z) {
        return updateVertexPos(vertex, x, y, z, 0);
    }

    public VertexBuffer updateVertexPos(int vertex, float x, float y, float z, int offset) {
        Preconditions.checkArgument(vertex < nowVertexes);
        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferSubData(GL_ARRAY_BUFFER, (long) vertex * size + offset, new float[]{x, y, z});
        return this;
    }

    public VertexBuffer updateVertexColor(int vertex, int color) {
        int R = (color >> 16) & 0xFF;
        int G = (color >> 8) & 0xFF;
        int B = color & 0xFF;
        return updateVertexColor(vertex, R / 255f, G / 255f, B / 255f);
    }

    public VertexBuffer updateVertexColor(int vertex, float r, float g, float b) {
        return updateVertexColor(vertex, r, g, b, 12);
    }

    public VertexBuffer updateVertexColor(int vertex, float r, float g, float b, int offset) {
        Preconditions.checkArgument(vertex < nowVertexes);
        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferSubData(GL_ARRAY_BUFFER, (long) vertex * size + offset, new float[]{r, g, b});
        return this;
    }

    public VertexBuffer updateVertexColorAlpha(int vertex, int color) {
        int R = (color >> 24) & 0xFF;
        int G = (color >> 16) & 0xFF;
        int B = (color >> 8) & 0xFF;
        int A = color & 0xFF;
        return updateVertexColorAlpha(vertex, R / 255f, G / 255f, B / 255f, A / 255f);
    }

    public VertexBuffer updateVertexColorAlpha(int vertex, float r, float g, float b, float a) {
        return updateVertexColorAlpha(vertex, r, g, b, a, 12);
    }

    public VertexBuffer updateVertexColorAlpha(int vertex, float r, float g, float b, float a, int offset) {
        Preconditions.checkArgument(vertex < nowVertexes);
        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferSubData(GL_ARRAY_BUFFER, (long) vertex * size + offset, new float[]{r, g, b, a});
        return this;
    }

    public VertexBuffer updateVertexUV(int vertex, float u, float v) {
        return updateVertexUV(vertex, u, v, 24);
    }

    public VertexBuffer updateVertexUV(int vertex, float u, float v, int offset) {
        Preconditions.checkArgument(vertex < nowVertexes);
        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferSubData(GL_ARRAY_BUFFER, (long) vertex * size + offset, new float[]{u, v});
        return this;
    }

    public VertexBuffer upload() {
        int pos = vertices.position();
        vertices.position(0);
        vertices.limit(pos);
        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferData(GL_ARRAY_BUFFER, vertices, mode);
        return this;
    }

    public VertexBuffer setPointers() {
        pointerSetter.run();
        return this;
    }

    public void destroy() {
        glDeleteBuffers(id);
    }
}
