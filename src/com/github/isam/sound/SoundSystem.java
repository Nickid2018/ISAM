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

import java.util.*;
import com.github.isam.phys.*;
import com.google.common.collect.*;

public class SoundSystem implements Runnable {

	public static void init() {
		new Thread(new SoundSystem(), "Sound Engine").start();
	}

	private static volatile boolean running = true;

	private static Queue<Runnable> runningQueue = Queues.newConcurrentLinkedQueue();
	private static Set<SoundInstance> sounds = Sets.newHashSet();
	private static Set<SoundInstance> toDelete = Sets.newHashSet();
	private static float volume = 1;
	private static Listener listener = new Listener();

	@Override
	public void run() {
		SoundEngine.initEngine();
		while (running) {
			while (!runningQueue.isEmpty())
				runningQueue.poll().run();
			for (SoundInstance instance : sounds)
				if (instance.doTick())
					toDelete.add(instance);
			sounds.removeAll(toDelete);
			toDelete.clear();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
		SoundEngine.stopEngine();
	}

	public static void enqueue(Runnable operation) {
		runningQueue.offer(operation);
	}

	public static SoundInstance create() {
		SoundInstance instance = SoundInstance.create();
		instance.setTotalVolume(volume);
		sounds.add(instance);
		return instance;
	}

	public static void setTotalVolume(float value) {
		enqueue(() -> {
			for (SoundInstance instance : sounds)
				instance.setTotalVolume(value);
			volume = value;
		});
	}

	public static void setListenerPos(Vec3f pos) {
		enqueue(() -> listener.setListenerPosition(pos));
	}

	public static void setListenerVelocity(Vec3f v) {
		enqueue(() -> listener.setListenerVelocity(v));
	}

	public static void setGain(float gain) {
		enqueue(() -> listener.setGain(gain));
	}

	public static void setOrientation(Vec3f at, Vec3f up) {
		enqueue(() -> listener.setOrientation(at, up));
	}

	public static Listener getListener() {
		return listener;
	}

	public static void stop() {
		running = false;
	}
}
