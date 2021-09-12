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

import com.github.isam.phys.Vec3f;

public class SoundProperties {

    public Vec3f position = Vec3f.ZERO.copy();
    public float pitch = 0;
    public boolean loop = false;
    public float volume = 1;
    public boolean relative = false;
    public float maxDistance = -1;

    public static SoundProperties create() {
        return new SoundProperties();
    }

    public SoundProperties withPosition(Vec3f position) {
        this.position = position;
        return this;
    }

    public SoundProperties wihPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public SoundProperties wihVolume(float volume) {
        this.volume = volume;
        return this;
    }

    public SoundProperties wihDistance(float distance) {
        this.maxDistance = distance;
        return this;
    }

    public SoundProperties withLoop() {
        loop = true;
        return this;
    }

    public SoundProperties withRelative() {
        relative = true;
        return this;
    }
}
