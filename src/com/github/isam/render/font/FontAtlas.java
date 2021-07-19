package com.github.isam.render.font;

import java.util.*;
import org.lwjgl.stb.*;
import java.util.concurrent.*;
import com.github.isam.phys.*;
import it.unimi.dsi.fastutil.ints.*;
import com.github.isam.render.texture.*;

public class FontAtlas {

	private Image image;
	private Int2ObjectMap<FontVertexInfos> chars = new Int2ObjectAVLTreeMap<>();
	private List<AABB> spareSpace = new ArrayList<>();
	private Queue<FontVertexInfos> toUpdates = new ConcurrentLinkedQueue<>();
	private Texture texture;
	private VertexFont font;

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
		if (widthLen == 0) {
			spareSpace.add(AABB.newAABB(minChunk.minX, minChunk.minY + height, minChunk.maxX, minChunk.maxY));
			sortAABBs();
			return Optional.of(info);
		}
		double heightLen = minChunk.getHeight() - height;
		if (heightLen == 0) {
			spareSpace.add(AABB.newAABB(minChunk.minX + width, minChunk.minY, minChunk.maxX, minChunk.maxY));
			sortAABBs();
			return Optional.of(info);
		}
		double area1 = widthLen * height;
		double area2 = heightLen * width;
		if (area1 > area2) {
			spareSpace.add(AABB.newAABB(minChunk.minX + width, minChunk.minY, minChunk.maxX, minChunk.maxY));
			spareSpace.add(AABB.newAABB(minChunk.minX, minChunk.minY + height, minChunk.minX + width, minChunk.maxY));
		} else {
			spareSpace.add(AABB.newAABB(minChunk.minX, minChunk.minY + height, minChunk.maxX, minChunk.maxY));
			spareSpace.add(AABB.newAABB(minChunk.minX + width, minChunk.minY, minChunk.maxX, minChunk.minY + height));
		}
		sortAABBs();
		return Optional.of(info);
	}

	private void sortAABBs() {
		spareSpace.sort((aabb1, aabb2) -> {
			double width1 = aabb1.getWidth();
			double width2 = aabb2.getWidth();
			double height1 = aabb1.getHeight();
			double height2 = aabb2.getHeight();
			double add1 = width1 + height1;
			double add2 = width2 + height2;
			if (add1 != add2)
				return (int) (add1 - add2);
			if (width1 != width2)
				return (int) (width1 - width2);
			if (height1 != height2)
				return (int) (height1 - height2);
			return 0;
		});
	}

	public Image getImage() {
		return image;
	}

	public FontVertexInfos getChar(int codepoint) {
		return chars.get(codepoint);
	}

	public void refresh() {
		if (texture == null)
			texture = new Texture(image, 0).setLinear(font.getSize() <= 32).setClamp(true);
		while (!toUpdates.isEmpty()) {
			FontVertexInfos info = toUpdates.poll();
			int x = (int) (info.minU * 1024);
			int y = (int) (info.minV * 1024);
			int xSize = (int) ((info.maxU - info.minU) * 1024);
			int ySize = (int) ((info.maxV - info.minV) * 1024);
			texture.update(x, y, xSize, ySize);
		}
	}

	public Texture getTexture() {
		refresh();
		return texture;
	}
}
