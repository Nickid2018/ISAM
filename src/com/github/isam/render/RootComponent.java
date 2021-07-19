package com.github.isam.render;

import java.util.*;
import com.github.isam.phys.*;

public class RootComponent extends ComponentContainer {

	private TreeSet<Component> components = new TreeSet<>((c1, c2) -> c1.zIndex - c2.zIndex);

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
