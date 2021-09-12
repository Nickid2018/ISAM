/*
 * Copyright 2021 ISAM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
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
 */
package com.github.isam.sound;

import java.util.concurrent.atomic.AtomicBoolean;

public class StreamSound {

    private volatile AsyncSoundInstance instance;
    private final AtomicBoolean created = new AtomicBoolean(false);

    public StreamSound(OggAudioStream stream, SoundProperties properties) {
        SoundSystem.enqueue(() -> {
            SoundInstance instance = SoundSystem.create();
            StreamSound.this.instance = new AsyncSoundInstance(instance);
            instance.attachBufferStream(stream);
            created.set(true);
            instance.setSelfPosition(properties.position);
            instance.setPitch(properties.pitch);
            instance.setLooping(properties.loop);
            instance.setRelative(properties.relative);
            instance.setVolume(properties.volume);
            if (properties.maxDistance > 0)
                instance.linearAttenuation(properties.maxDistance);
        });
    }

    public void play() {
        if (created.get())
            instance.play();
    }

    public AsyncSoundInstance getInstance() {
        return created.get() ? instance : null;
    }
}
