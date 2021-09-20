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

package com.github.isam.render.gui;

import com.github.isam.phys.AABB;
import com.github.isam.render.Renderer;
import com.github.isam.render.font.VertexFont;

public class TextLabel extends TextComponent {

    public TextLabel(Renderer renderer, AABB position, VertexFont font) {
        super(renderer, position, font);
    }

    public TextLabel(Renderer renderer, AABB position, VertexFont font, String text) {
        super(renderer, position, font, text);
    }

    public TextLabel(Renderer renderer, AABB position, VertexFont font, String text, int color) {
        super(renderer, position, font, text, color);
    }

    @Override
    public void onResize(int sWidth, int sHeight, int reWidth, int reHeight) {

    }

}
