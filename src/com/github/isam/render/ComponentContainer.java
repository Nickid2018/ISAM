package com.github.isam.render;

import java.util.*;
import com.github.isam.phys.*;
import com.github.isam.input.*;

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
