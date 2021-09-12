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

import com.github.isam.input.MouseEvent;
import com.github.isam.phys.AABB;

import java.util.TreeSet;

public abstract class ComponentContainer extends Component {

    public ComponentContainer(Renderer renderer, AABB position) {
        super(renderer, position);
    }

    public abstract TreeSet<Component> getComponents();

    public abstract void addComponent(Component component);

    public abstract void removeComponent(Component component);

    @Override
    public void render() {
        getComponents().forEach(Component::render);
    }

    @Override
    public void onResize(int sWidth, int sHeight, int reWidth, int reHeight) {
        getComponents().forEach(component -> component.onResize(sWidth, sHeight, reWidth, reHeight));
    }

    @Override
    public Component getComponentTouched(int xpos, int ypos) {
        Component now = null;
        for (Component component : getComponents())
            if ((now = component.getComponentTouched(xpos, ypos)) != null)
                break;
        return now != null ? now : (MouseEvent.checkInRange(position, xpos, ypos) ? this : null);
    }
}
