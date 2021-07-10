/*
 * Copyright 2021 ISAM
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
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
 * 
 */
package com.github.isam.sound;

import java.io.*;
import java.nio.*;
import java.util.*;
import javax.sound.sampled.*;
import com.google.common.base.*;

import static org.lwjgl.openal.AL10.*;

public class SoundBuffer {

	private ByteBuffer buffer;
	private AudioFormat format;
	private int id;

	public SoundBuffer(ByteBuffer buffer, AudioFormat format) {
		this.buffer = buffer;
		this.format = format;
	}

	public SoundBuffer(OggAudioStream stream) {
		try {
			buffer = stream.readAll();
			format = stream.getFormat();
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		id = -1;
	}

	public void setStream(OggAudioStream stream) {
		try {
			Preconditions.checkState(!isAlive());
			buffer = stream.readAll();
			format = stream.getFormat();
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void init() {
		if (isAlive())
			return;
		id = alGenBuffers();
		if (SoundEngine.checkALError("generating buffer"))
			return;
		int type = audioFormatToOpenAl(format);
		alBufferData(id, type, buffer, (int) format.getSampleRate());
		if (SoundEngine.checkALError("assigning buffer data"))
			return;
	}

	public void delete() {
		alDeleteBuffers(id);
		if (SoundEngine.checkALError("deleting buffer"))
			return;
		id = -1;
	}

	public OptionalInt release() {
		init();
		OptionalInt handle = getHandle();
		id = -1;
		return handle;
	}

	public boolean isAlive() {
		return id != -1;
	}

	public OptionalInt getHandle() {
		return isAlive() ? OptionalInt.of(id) : OptionalInt.empty();
	}

	private static int audioFormatToOpenAl(AudioFormat format) {
		AudioFormat.Encoding encode = format.getEncoding();
		int channels = format.getChannels();
		int size = format.getSampleSizeInBits();
		if (encode.equals(AudioFormat.Encoding.PCM_UNSIGNED) || encode.equals(AudioFormat.Encoding.PCM_SIGNED)) {
			if (channels == 1) {
				if (size == 8)
					return AL_FORMAT_MONO8;
				if (size == 16)
					return AL_FORMAT_MONO16;
			} else if (channels == 2) {
				if (size == 8)
					return AL_FORMAT_STEREO8;
				if (size == 16)
					return AL_FORMAT_STEREO16;
			}
		}
		throw new IllegalArgumentException("Invalid audio format");
	}
}
