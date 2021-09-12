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

public class Vec2f {

    public static final Vec2f ZERO = new Vec2f(0, 0);
    public static final Vec2f POSITIVE_X = new Vec2f(1, 0);
    public static final Vec2f NEGATIVE_X = new Vec2f(-1, 0);
    public static final Vec2f POSITIVE_Y = new Vec2f(0, 1);
    public static final Vec2f NEGATIVE_Y = new Vec2f(0, -1);

    public float x;
    public float y;

    public Vec2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2f copy() {
        return new Vec2f(x, y);
    }

    public Vec3f as3() {
        return new Vec3f(x, y, 0);
    }

    public Vec2f move(float xa, float ya) {
        x += xa;
        y += ya;
        return this;
    }

    public Vec2f add(Vec2f vec) {
        return move(vec.x, vec.y);
    }

    public Vec2f normalize() {
        float normal = getNormal();
        x /= normal;
        y /= normal;
        return this;
    }

    public float getNormal() {
        return (float) Math.sqrt(x * x + y * y);
    }
}
