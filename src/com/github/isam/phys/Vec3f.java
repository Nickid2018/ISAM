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

package com.github.isam.phys;

public class Vec3f {

    public static final Vec3f ZERO = new Vec3f(0, 0, 0);
    public static final Vec3f POSITIVE_X = new Vec3f(1, 0, 0);
    public static final Vec3f NEGATIVE_X = new Vec3f(-1, 0, 0);
    public static final Vec3f POSITIVE_Y = new Vec3f(0, 1, 0);
    public static final Vec3f NEGATIVE_Y = new Vec3f(0, -1, 0);
    public static final Vec3f POSITIVE_Z = new Vec3f(0, 0, 1);
    public static final Vec3f NEGATIVE_Z = new Vec3f(0, 0, -1);

    public float x;
    public float y;
    public float z;

    public Vec3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3f copy() {
        return new Vec3f(x, y, z);
    }

    public Vec3f move(float xa, float ya, float za) {
        x += xa;
        y += ya;
        z += za;
        return this;
    }

    public Vec3f add(Vec3f vec) {
        return move(vec.x, vec.y, vec.z);
    }

    public Vec3f normalize() {
        float normal = getNormal();
        x /= normal;
        y /= normal;
        z /= normal;
        return this;
    }

    public float getNormal() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }
}
