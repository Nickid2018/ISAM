package com.github.isam.render.texture;

import java.io.*;
import java.nio.*;
import org.lwjgl.system.*;

import com.github.isam.render.window.Window;

import java.nio.channels.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL14.*;

public class TextureUtil {

	public static int generateTextureId() {
		return glGenTextures();
	}

	public static void releaseTextureId(int id) {
		glDeleteTextures(id);
	}

	private static void bind(int id) {
		glBindTexture(GL_TEXTURE_2D, id);
	}

	public static void prepareImage(int id, int width, int height) {
		prepareImage(Texture.InternalGlFormat.RGBA, id, 0, width, height);
	}

	public static void prepareImage(Texture.InternalGlFormat format, int id, int width, int height) {
		prepareImage(format, id, 0, width, height);
	}

	public static void prepareImage(int id, int mipmap, int width, int height) {
		prepareImage(Texture.InternalGlFormat.RGBA, id, mipmap, width, height);
	}

	public static void prepareImage(Texture.InternalGlFormat format, int id, int mipmap, int width, int height) {
		bind(id);
		if (mipmap >= 0) {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, mipmap);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_LOD, 0);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LOD, mipmap);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, 0.0F);
		}
		for (int level = 0; level <= mipmap; level++)
			glTexImage2D(GL_TEXTURE_2D, level, format.glFormat(), width >> level, height >> level, 0, GL_RGBA,
					GL_UNSIGNED_BYTE, 0);
	}

	public static ByteBuffer readResource(InputStream inputStream) throws IOException {
		ByteBuffer buffer;
		if (inputStream instanceof FileInputStream) {
			FileInputStream fileInputStream = (FileInputStream) inputStream;
			FileChannel fileChannel = fileInputStream.getChannel();
			buffer = MemoryUtil.memAlloc((int) fileChannel.size() + 1);
			while (fileChannel.read(buffer) != -1)
				;
		} else {
			buffer = MemoryUtil.memAlloc(8192);
			ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
			while (readableByteChannel.read(buffer) != -1)
				if (buffer.remaining() == 0)
					buffer = MemoryUtil.memRealloc(buffer, buffer.capacity() * 2);
		}
		return buffer;
	}

	public static void writeAsPNG(String path, int id, int levels, int width, int height) {
		bind(id);
		for (int level = 0; level <= levels; level++) {
			String file = path + "_" + level + ".png";
			int sizeX = width >> level;
			int sizeY = height >> level;
			try (Texture texture = new Texture(sizeX, sizeY, false)) {
				texture.downloadTexture(level, false);
				texture.writeToFile(file);
				Window.LOGGER.debug("Exported png to: " + new File(file).getAbsolutePath());
			} catch (IOException e) {
				Window.LOGGER.debug("Unable to write: ", e);
			}
		}
	}

	public static void initTexture(IntBuffer pixels, int width, int height) {
		glPixelStorei(GL_UNPACK_SWAP_BYTES, 0);
		glPixelStorei(GL_UNPACK_LSB_FIRST, 0);
		glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
		glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
		glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 4);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, pixels);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	}
}