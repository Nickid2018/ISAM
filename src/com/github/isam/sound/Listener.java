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

import static org.lwjgl.openal.AL10.*;

public class Listener {

    private Vec3f position;
    private Vec3f velocity;
    private Vec3f oriAt;
    private Vec3f oriUp;
    private float gain;

    public Listener() {
        position = Vec3f.ZERO.copy();
        velocity = Vec3f.ZERO.copy();
        oriAt = Vec3f.NEGATIVE_Z.copy();
        oriUp = Vec3f.POSITIVE_Y.copy();
        gain = 1;
    }

    public Vec3f getListenerPosition() {
        return position;
    }

    public void setListenerPosition(Vec3f position) {
        this.position = position;
        alListener3f(AL_POSITION, position.x, position.y, position.z);
    }

    public Vec3f getListenerVelocity() {
        return velocity;
    }

    public void setListenerVelocity(Vec3f velocity) {
        this.velocity = velocity;
        alListener3f(AL_VELOCITY, velocity.x, velocity.y, velocity.z);
    }

    public void setOrientation(Vec3f at, Vec3f up) {
        oriAt = at;
        oriUp = up;
        alListenerfv(AL_ORIENTATION, new float[]{at.x, at.y, at.z, up.x, up.y, up.z});
    }

    public Vec3f[] getOrientation() {
        return new Vec3f[]{oriAt, oriUp};
    }

    public float getGain() {
        return gain;
    }

    public void setGain(float gain) {
        this.gain = gain;
        alListenerf(AL_GAIN, gain);
    }

    public void reset() {
        setListenerPosition(Vec3f.ZERO.copy());
        setListenerVelocity(Vec3f.ZERO.copy());
        setOrientation(Vec3f.NEGATIVE_Z.copy(), Vec3f.POSITIVE_Y.copy());
    }
}
