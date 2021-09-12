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
package com.github.isam.render.window;

import org.lwjgl.glfw.GLFWVidMode;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VideoMode {

    private static final Pattern PATTERN = Pattern.compile("(\\d+)x(\\d+)(?:@(\\d+)(?::(\\d+))?)?");
    private final int width;
    private final int height;
    private final int redBits;
    private final int greenBits;
    private final int blueBits;
    private final int refreshRate;

    public VideoMode(int width, int height, int redBits, int greenBits, int blueBits, int refreshRate) {
        this.width = width;
        this.height = height;
        this.redBits = redBits;
        this.greenBits = greenBits;
        this.blueBits = blueBits;
        this.refreshRate = refreshRate;
    }

    public VideoMode(GLFWVidMode.Buffer buffer) {
        width = buffer.width();
        height = buffer.height();
        redBits = buffer.redBits();
        greenBits = buffer.greenBits();
        blueBits = buffer.blueBits();
        refreshRate = buffer.refreshRate();
    }

    public VideoMode(GLFWVidMode gLfWvidMode) {
        width = gLfWvidMode.width();
        height = gLfWvidMode.height();
        redBits = gLfWvidMode.redBits();
        greenBits = gLfWvidMode.greenBits();
        blueBits = gLfWvidMode.blueBits();
        refreshRate = gLfWvidMode.refreshRate();
    }

    public static Optional<VideoMode> read(@Nullable String string) {
        if (string == null)
            return Optional.empty();
        try {
            Matcher matcher = PATTERN.matcher(string);
            if (matcher.matches()) {
                int width = Integer.parseInt(matcher.group(1));
                int height = Integer.parseInt(matcher.group(2));
                String refrashRateStr = matcher.group(3);
                int refrashRate = refrashRateStr == null ? 60 : Integer.parseInt(refrashRateStr);
                String colorBitStr = matcher.group(4);
                int colorBits = colorBitStr == null ? 24 : Integer.parseInt(colorBitStr);
                int colorBit = colorBits / 3;
                return Optional.of(new VideoMode(width, height, colorBit, colorBit, colorBit, refrashRate));
            }
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRedBits() {
        return redBits;
    }

    public int getGreenBits() {
        return greenBits;
    }

    public int getBlueBits() {
        return blueBits;
    }

    public int getRefreshRate() {
        return refreshRate;
    }

    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        VideoMode videoMode = (VideoMode) object;
        return width == videoMode.width && height == videoMode.height && redBits == videoMode.redBits
                && greenBits == videoMode.greenBits && blueBits == videoMode.blueBits
                && refreshRate == videoMode.refreshRate;
    }

    public int hashCode() {
        return Objects.hash(width, height, redBits, greenBits, blueBits, refreshRate);
    }

    public String toString() {
        return String.format("%sx%s@%s (%sbit)", width, height, refreshRate, redBits + greenBits + blueBits);
    }

    public String write() {
        return String.format("%sx%s@%s:%s", width, height, refreshRate, redBits + greenBits + blueBits);
    }
}