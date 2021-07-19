package com.github.isam.render.gui;

import com.github.isam.phys.AABB;
import com.github.isam.render.Component;
import com.github.isam.render.Renderer;
import com.github.isam.render.SimpleRenderer;
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
		// TODO 自动生成的方法存根

	}

}
