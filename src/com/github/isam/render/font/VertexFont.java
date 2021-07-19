package com.github.isam.render.font;

import java.io.*;
import java.nio.*;
import java.util.*;
import org.lwjgl.stb.*;
import org.lwjgl.system.*;
import it.unimi.dsi.fastutil.ints.*;
import com.google.common.collect.*;
import com.github.isam.render.texture.*;

public class VertexFont {

	public static void main(String[] args) throws Exception {
		VertexFont font = new VertexFont(new FileInputStream("C:\\Windows\\Fonts\\MSYH.TTF"), 72);
		System.out.println("Font loaded!");
		System.out.println(font.getSpaceLength());
	}

	private STBTTFontinfo font;
	private ByteBuffer buffer;
	private List<FontAtlas> atlases = Lists.newArrayList();
	private Int2ObjectMap<FontAtlas> chars = new Int2ObjectAVLTreeMap<>();
	private float ascent;
	private float descent;
	private float lineGap;
	private float spaceLength;
	private float scale;
	private final int size;

	public VertexFont(InputStream fontFile, int size) throws IOException {
		this.size = size;
		font = STBTTFontinfo.create();
		buffer = TextureUtil.readResource(fontFile);
		buffer.rewind();
		if (!STBTruetype.stbtt_InitFont(font, buffer))
			throw new IOException("Can't initialize font");
		scale = STBTruetype.stbtt_ScaleForPixelHeight(font, size);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer ascentBuf = stack.mallocInt(1);
			IntBuffer descentBuf = stack.mallocInt(1);
			IntBuffer lineGapBuf = stack.mallocInt(1);
			STBTruetype.stbtt_GetFontVMetrics(font, ascentBuf, descentBuf, lineGapBuf);
			ascent = ascentBuf.get(0) * scale;
			descent = descentBuf.get(0) * scale;
			lineGap = lineGapBuf.get(0) * scale;
			IntBuffer advanceWidthBuf = stack.mallocInt(1);
			IntBuffer leftSideBearingBuf = stack.mallocInt(1);
			STBTruetype.stbtt_GetCodepointHMetrics(font, ' ', advanceWidthBuf, leftSideBearingBuf);
			spaceLength = advanceWidthBuf.get(0) * scale;
		}
	}

	public int getSize() {
		return size;
	}

	public float getAscent() {
		return ascent;
	}

	public float getDescent() {
		return descent;
	}

	public float getLineGap() {
		return lineGap;
	}

	public float getSpaceLength() {
		return spaceLength;
	}

	public FontVertexInfos getCodepointInfo(int codepoint) {
		if (chars.containsKey(codepoint))
			return chars.get(codepoint).getChar(codepoint);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer leftBuf = stack.mallocInt(1);
			IntBuffer buttomBuf = stack.mallocInt(1);
			IntBuffer rightBuf = stack.mallocInt(1);
			IntBuffer topBuf = stack.mallocInt(1);
			STBTruetype.stbtt_GetCodepointBitmapBox(font, codepoint, scale, scale, leftBuf, buttomBuf, rightBuf,
					topBuf);
			IntBuffer advanceWidthBuf = stack.mallocInt(1);
			IntBuffer leftSideBearingBuf = stack.mallocInt(1);
			STBTruetype.stbtt_GetCodepointHMetrics(font, codepoint, advanceWidthBuf, leftSideBearingBuf);
			int left = leftBuf.get(0);
			int buttom = buttomBuf.get(0);
			int right = rightBuf.get(0);
			int top = topBuf.get(0);
			int width = right - left;
			int height = top - buttom;
			int advanceWidth = advanceWidthBuf.get(0);
			int leftSideBearing = leftSideBearingBuf.get(0);
			float convertL = leftSideBearing * scale;
			float convertA = advanceWidth * scale;
			if (width == 0 || height == 0)
				throw new IllegalArgumentException("Can't generate the font bitmap - unrecorded character");
			for (FontAtlas atlas : atlases) {
				Optional<FontVertexInfos> optional = atlas.putBitmap(font, codepoint, scale, width, height, left, top,
						convertL, convertA, ascent + buttom);
				if (optional.isPresent()) {
					chars.put(codepoint, atlas);
					return optional.get();
				}
			}
			FontAtlas atlas = new FontAtlas(this);
			Optional<FontVertexInfos> optional = atlas.putBitmap(font, codepoint, scale, width, height, left, top,
					convertL, convertA, ascent + buttom);
			if (optional.isPresent()) {
				atlases.add(atlas);
				chars.put(codepoint, atlas);
				return optional.get();
			}
		}
		throw new IllegalArgumentException("Can't generate the font bitmap - too large");
	}
}
