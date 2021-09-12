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

package com.github.isam.render;

import com.github.isam.input.KeyEvent;
import com.github.isam.input.MouseEvent;
import com.github.isam.phys.AABB;
import com.github.isam.render.shader.Shaders;
import com.github.isam.render.vertex.ElementBuffer;
import com.github.isam.render.vertex.VertexArray;
import com.github.isam.render.vertex.VertexBuffer;
import com.github.isam.render.window.Cursors;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;

public abstract class Component {

    protected final Renderer renderer;
    protected final AABB position;
    protected boolean focused;
    protected int zIndex;

    public Component(Renderer renderer, AABB position) {
        this.renderer = renderer;
        this.position = position;
    }

    public static VertexArray prepareTextureRender(float x1, float y1, float x2, float y2, float tx1, float ty1, float tx2,
                                                   float ty2, boolean dynamic) {
        VertexBuffer buffer = new VertexBuffer(4, dynamic ? GL30.GL_STREAM_DRAW : GL30.GL_STATIC_DRAW);
        buffer.pos(x1, y1, 0).color(1, 1, 1).uv(tx1, ty2).endVertex();
        buffer.pos(x2, y1, 0).color(1, 1, 1).uv(tx2, ty2).endVertex();
        buffer.pos(x2, y2, 0).color(1, 1, 1).uv(tx2, ty1).endVertex();
        buffer.pos(x1, y2, 0).color(1, 1, 1).uv(tx1, ty1).endVertex();
        ElementBuffer ebo = new ElementBuffer(2);
        ebo.putTriangle(0, 1, 3);
        ebo.putTriangle(1, 2, 3);
        VertexArray array = new VertexArray(Shaders.SIMPLE);
        array.bindVBO(buffer);
        array.bindEBO(ebo);
        array.upload();
        return array;
    }

    public abstract void render();

    public abstract void onResize(int sWidth, int sHeight, int reWidth, int reHeight);

    public Component getComponentTouched(int xpos, int ypos) {
        return MouseEvent.checkInRange(position, xpos, ypos) ? this : null;
    }

    public boolean isFocused() {
        return focused;
    }

    public Component setFocused(boolean focused) {
        this.focused = focused;
        return this;
    }

    public int getZIndex() {
        return zIndex;
    }

    public Component setZIndex(int zIndex) {
        this.zIndex = zIndex;
        return this;
    }

    public void onFocus(boolean focus) {
    }

    public void onMouseEnter() {
        if (getCursorInComponent() != -1)
            GLFW.glfwSetCursor(renderer.getWindow().getHandle(), getCursorInComponent());
    }

    public long getCursorInComponent() {
        return -1;
    }

    public void onMouseLeave() {
        if (getCursorInComponent() != -1)
            GLFW.glfwSetCursor(renderer.getWindow().getHandle(), Cursors.DEFAULT_CURSOR);
    }

    public void onMouseEvent(MouseEvent event) {

    }

    public void onKeyEvent(KeyEvent event) {
    }
}
