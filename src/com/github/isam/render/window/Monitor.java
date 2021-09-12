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

import com.google.common.collect.Lists;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.List;
import java.util.Optional;

public final class Monitor {

    private final long monitor;

    private final List<VideoMode> videoModes;

    private VideoMode currentMode;

    private int x;

    private int y;

    public Monitor(long l) {
        monitor = l;
        videoModes = Lists.newArrayList();
        refreshVideoModes();
    }

    private void refreshVideoModes() {
        this.videoModes.clear();
        GLFWVidMode.Buffer buffer = GLFW.glfwGetVideoModes(monitor);
        for (int i = buffer.limit() - 1; i >= 0; i--) {
            buffer.position(i);
            VideoMode videoMode = new VideoMode(buffer);
            if (videoMode.getRedBits() >= 8 && videoMode.getGreenBits() >= 8 && videoMode.getBlueBits() >= 8)
                videoModes.add(videoMode);
        }
        int[] arrayInt = new int[1];
        int[] arrayInt1 = new int[1];
        GLFW.glfwGetMonitorPos(this.monitor, arrayInt, arrayInt1);
        x = arrayInt[0];
        y = arrayInt1[0];
        GLFWVidMode gLfWvidMode = GLFW.glfwGetVideoMode(monitor);
        currentMode = new VideoMode(gLfWvidMode);
    }

    public VideoMode getPreferredVidMode(Optional<VideoMode> optional) {
        if (optional.isPresent()) {
            VideoMode videoMode = optional.get();
            for (VideoMode videoMode1 : this.videoModes) {
                if (videoMode1.equals(videoMode))
                    return videoMode1;
            }
        }
        return getCurrentMode();
    }

    public int getVideoModeIndex(VideoMode videoMode) {
        return this.videoModes.indexOf(videoMode);
    }

    public VideoMode getCurrentMode() {
        return this.currentMode;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public VideoMode getMode(int i) {
        return videoModes.get(i);
    }

    public int getModeCount() {
        return videoModes.size();
    }

    public long getMonitor() {
        return monitor;
    }

    public String toString() {
        return String.format("Monitor[%s %sx%s %s]", monitor, x, y, currentMode);
    }
}