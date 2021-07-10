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

import com.github.isam.phys.*;

public class AsyncSoundInstance {

	private SoundInstance instance;

	public AsyncSoundInstance(SoundInstance instance) {
		this.instance = instance;
	}

	public void play() {
		SoundSystem.enqueue(instance::play);
	}

	public void pause() {
		SoundSystem.enqueue(instance::pause);
	}

	public void unpause() {
		SoundSystem.enqueue(instance::unpause);
	}

	public void stop() {
		SoundSystem.enqueue(instance::stop);
	}

	public void setSelfPosition(Vec3f position) {
		SoundSystem.enqueue(() -> instance.setSelfPosition(position));
	}

	public void setPitch(float pitch) {
		SoundSystem.enqueue(() -> instance.setPitch(pitch));
	}

	public void setLooping(boolean loop) {
		SoundSystem.enqueue(() -> instance.setLooping(loop));
	}

	public void setVolume(float volume) {
		SoundSystem.enqueue(() -> instance.setVolume(volume));
	}

	public void disableAttenuation() {
		SoundSystem.enqueue(instance::disableAttenuation);
	}

	public void linearAttenuation(float maxDistance) {
		SoundSystem.enqueue(() -> instance.linearAttenuation(maxDistance));
	}

	public void setRelative(boolean relative) {
		SoundSystem.enqueue(() -> instance.setRelative(relative));
	}
}
