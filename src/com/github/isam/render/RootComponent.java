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

import com.github.isam.phys.AABB;

import java.util.TreeSet;

public class RootComponent extends ComponentContainer {

    private final TreeSet<Component> components = new TreeSet<>((c1, c2) -> c1.zIndex - c2.zIndex);

    public RootComponent(Renderer renderer) {
        super(renderer, AABB.newAABB(0, 0, renderer.getWindow().getWidth(), renderer.getWindow().getHeight()));
    }

    @Override
    public TreeSet<Component> getComponents() {
        return components;
    }

    @Override
    public void addComponent(Component component) {
        components.add(component);
    }

    @Override
    public void onResize(int sWidth, int sHeight, int reWidth, int reHeight) {
        position.maxX = reWidth;
        position.maxY = reHeight;
        super.onResize(sWidth, sHeight, reWidth, reHeight);
    }

    @Override
    public void removeComponent(Component component) {
        components.remove(component);
    }
}
