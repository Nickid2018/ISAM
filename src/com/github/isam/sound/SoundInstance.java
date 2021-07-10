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
import javax.annotation.*;
import javax.sound.sampled.*;
import com.github.isam.phys.*;
import java.util.concurrent.atomic.*;

import static org.lwjgl.openal.AL11.*;

public class SoundInstance {

	private final int source;

	private AtomicBoolean initialized = new AtomicBoolean(true);

	private int streamingBufferSize = 16384;

	private float baseVolume;
	private float totalVolume;

	@Nullable
	private OggAudioStream stream;

	@Nullable
	public static SoundInstance create() {
		int source = alGenSources();
		return SoundEngine.checkALError("allocating new source") ? null : new SoundInstance(source);
	}

	private SoundInstance(int source) {
		this.source = source;
	}

	public void setTotalVolume(float volume) {
		totalVolume = volume;
		setVolume(baseVolume);
	}

	public void destroy() {
		if (initialized.compareAndSet(true, false)) {
			alSourceStop(source);
			SoundEngine.checkALError("stoping");
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					SoundEngine.LOGGER.error("Failed to close audio stream", e);
				}
				removeProcessedBuffers();
				stream = null;
			}
			alDeleteSources(source);
			SoundEngine.checkALError("cleaning up");
		}
	}

	public void play() {
		alSourcePlay(source);
	}

	private int getState() {
		return !initialized.get() ? AL_STOPPED : alGetSourcei(source, AL_SOURCE_STATE);
	}

	public void pause() {
		if (getState() == AL_PLAYING)
			alSourcePause(source);
	}

	public void unpause() {
		if (getState() == AL_PAUSED)
			alSourcePlay(source);
	}

	public void stop() {
		if (initialized.get()) {
			alSourceStop(source);
			SoundEngine.checkALError("stoping");
		}
	}

	public boolean stopped() {
		return getState() == AL_STOPPED;
	}

	public void setSelfPosition(Vec3f position) {
		alSourcefv(source, AL_POSITION, new float[] { position.x, position.y, position.z });
	}

	public void setPitch(float pitch) {
		alSourcef(source, AL_PITCH, pitch);
	}

	public void setLooping(boolean loop) {
		alSourcei(source, AL_LOOPING, loop ? 1 : 0);
	}

	public void setVolume(float volume) {
		alSourcef(source, AL_GAIN, (baseVolume = volume) * totalVolume);
	}

	public void disableAttenuation() {
		alSourcei(source, AL_DISTANCE_MODEL, 0);
	}

	public void linearAttenuation(float maxDistance) {
		alSourcei(source, AL_DISTANCE_MODEL, 53251);
		alSourcef(source, AL_MAX_DISTANCE, maxDistance);
		alSourcef(source, AL_ROLLOFF_FACTOR, 1.0F);
		alSourcef(source, AL_REFERENCE_DISTANCE, 0.0F);
	}

	public void setRelative(boolean relative) {
		alSourcei(source, AL_SOURCE_RELATIVE, relative ? 1 : 0);
	}

	public void attachStaticBuffer(SoundBuffer soundBuffer) {
		soundBuffer.getHandle().ifPresent(i -> alSourcei(source, AL_BUFFER, i));
	}

	public void attachBufferStream(OggAudioStream audioStream) {
		stream = audioStream;
		AudioFormat audioFormat = audioStream.getFormat();
		streamingBufferSize = calculateBufferSize(audioFormat, 1);
		pumpBuffers(4);
	}

	public boolean doTick() {
		if (initialized.get() && stopped()) {
			destroy();
			return true;
		}
		if (getState() == AL_PLAYING && isStreamed())
			updateStream();
		return false;
	}

	private static int calculateBufferSize(AudioFormat audioFormat, int i) {
		return (int) ((i * audioFormat.getSampleSizeInBits()) / 8.0F * audioFormat.getChannels()
				* audioFormat.getSampleRate());
	}

	private void pumpBuffers(int buffers) {
		if (stream != null)
			try {
				for (int index = 0; index < buffers; index++) {
					ByteBuffer buffer = stream.read(streamingBufferSize);
					if (buffer != null)
						(new SoundBuffer(buffer, stream.getFormat())).release()
								.ifPresent(id -> alSourceQueueBuffers(source, id));
				}
			} catch (IOException e) {
				SoundEngine.LOGGER.error("Failed to read from audio stream", e);
			}
	}

	public void updateStream() {
		if (stream != null) {
			int buffers = removeProcessedBuffers();
			pumpBuffers(buffers);
		}
	}

	public boolean isStreamed() {
		return stream != null;
	}

	private int removeProcessedBuffers() {
		int bufferSize = alGetSourcei(source, AL_BUFFERS_PROCESSED);
		if (bufferSize > 0) {
			int[] buffers = new int[bufferSize];
			alSourceUnqueueBuffers(source, buffers);
			SoundEngine.checkALError("Unqueue buffers");
			alDeleteBuffers(buffers);
			SoundEngine.checkALError("Remove processed buffers");
		}
		return bufferSize;
	}

}
