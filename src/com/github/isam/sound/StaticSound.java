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

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class StaticSound {

    @Nullable
    private SoundBuffer buffer;
    private final AtomicBoolean created = new AtomicBoolean(false);

    public StaticSound(OggAudioStream stream) {
        SoundSystem.enqueue(() -> {
            buffer = new SoundBuffer(stream);
            buffer.init();
            created.set(true);
        });
    }

    public void playSound(SoundProperties properties) {
        playSound(properties, null);
    }

    public void playSound(SoundProperties properties, Consumer<SoundInstance> user) {
        if (!created.get())
            return;
        SoundSystem.enqueue(() -> {
            SoundInstance instance = SoundSystem.create();
            instance.attachStaticBuffer(buffer);
            instance.setSelfPosition(properties.position);
            instance.setPitch(properties.pitch);
            instance.setLooping(properties.loop);
            instance.setRelative(properties.relative);
            instance.setVolume(properties.volume);
            if (properties.maxDistance > 0)
                instance.linearAttenuation(properties.maxDistance);
            instance.play();
            if (user != null)
                user.accept(instance);
        });
    }
}
