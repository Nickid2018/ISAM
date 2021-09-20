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
package com.github.isam;

import com.github.isam.crash.CrashReport;
import com.github.isam.phys.AABB;
import com.github.isam.render.Component;
import com.github.isam.render.Renderer;
import com.github.isam.render.SimpleRenderer;
import com.github.isam.render.font.VertexFont;
import com.github.isam.render.gui.TextLabel;
import com.github.isam.render.texture.DynamicImage;
import com.github.isam.render.texture.DynamicTexture;
import com.github.isam.render.vertex.VertexArray;
import com.github.isam.render.window.DisplayData;
import com.github.isam.render.window.Window;
import com.github.isam.render.window.WindowEventListener;
import com.github.isam.sound.OggAudioStream;
import com.github.isam.sound.SoundProperties;
import com.github.isam.sound.SoundSystem;
import com.github.isam.sound.StaticSound;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

public class ISAM {

    private static ISAM instance;
    private Renderer renderer;
    private Window window;

    private ISAM() {
        initializeThrowableListener();
    }

    public static void main(String[] args) {
        instance = new ISAM();
        instance.initGLAndRun();
    }

    public static ISAM getInstance() {
        return instance;
    }

    public static void initializeThrowableListener() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            CrashReport report = new CrashReport("Final throwable tracker", e);
            report.writeToFile();
            System.exit(-1);
        });
    }

    // This is a test of rendering
    private void initGLAndRun() {
        DisplayData data = new DisplayData();
        data.frameLimit = 60;
        data.fullScreen = false;
        data.vsync = true;
        data.width = 1300;
        data.height = 1200;
        window = new Window("ISAM", data, new WindowEventListener() {

            @Override
            public void onResizeDisplay(int sWidth, int sHeight, int reWidth, int reHeight) {
                System.out.println("resize!");
            }

            @Override
            public void onFocus(boolean focus) {
                System.out.println("focus update! " + focus);
            }
        });

        Thread.currentThread().setName("Render Thread");

        SoundSystem.init();
//
        DynamicImage texture;
        try {
            texture = new DynamicImage(new FileInputStream("D:\\testFiles\\rick astley.gif"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//		StaticTexture tex = new StaticTexture(texture, 4).setLinear(true).update();
        StaticSound sound = null;
        try {
            sound = new StaticSound(
                    new OggAudioStream(new FileInputStream("D:\\testFiles\\Never gonna give you up.ogg")));
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DynamicTexture tex;
        try {
            tex = new DynamicTexture(texture, 4).setLinear(true).update();
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }

        sound.playSound(SoundProperties.create().wihPitch(1).wihVolume(0.02f).withLoop());
        VertexArray array2 = Component.prepareTextureRender(-1, -1, 1, 1, 0, 0, 1, 1, false);

        SimpleRenderer renderer = new SimpleRenderer(window);
        VertexFont font = null;
        try {
            font = new VertexFont(new FileInputStream("C:\\Windows\\Fonts\\STKAITI.TTF"), 64);
        } catch (IOException e) {
            e.printStackTrace();
        }
        renderer.getRoot().addComponent(
                new TextLabel(renderer, AABB.newAABB(0, 0, 200, 64), font, "We were no strangers to love", 0x0000FF00));
        renderer.getRoot().addComponent(new TextLabel(renderer, AABB.newAABB(0, 64, 200, 128), font,
                "You know the rules and so do I", 0x00FF0000).setZIndex(1));
        renderer.getRoot().addComponent(new TextLabel(renderer, AABB.newAABB(0, 128, 200, 192), font,
                "A full commitments what I'm thinking of", 0x000000FF).setZIndex(2));
        renderer.getRoot().addComponent(new TextLabel(renderer, AABB.newAABB(0, 192, 200, 256), font,
                "You would't get this from any other guy", 0x00F0F000).setZIndex(3));
        renderer.getRoot().addComponent(new TextLabel(renderer, AABB.newAABB(0, 256, 200, 320), font,
                "I just wanna tell you how I'm feeling", 0x00F00F00).setZIndex(4));
        renderer.getRoot().addComponent(
                new TextLabel(renderer, AABB.newAABB(0, 320, 200, 384), font, "Gotta make you understand", 0x00F000F0)
                        .setZIndex(5));
        renderer.getRoot().addComponent(
                new TextLabel(renderer, AABB.newAABB(0, 384, 200, 448), font, "Never gonna give you up", 0x00F0000F)
                        .setZIndex(6));
        renderer.getRoot().addComponent(
                new TextLabel(renderer, AABB.newAABB(0, 448, 200, 512), font, "Never gonna let you down", 0x000FF000)
                        .setZIndex(7));
        renderer.getRoot().addComponent(new TextLabel(renderer, AABB.newAABB(0, 512, 200, 576), font,
                "Never gonna run around and desert you", 0x000F0F00).setZIndex(8));
        renderer.getRoot().addComponent(
                new TextLabel(renderer, AABB.newAABB(0, 576, 200, 640), font, "Never gonna make you cry", 0x000F00F0)
                        .setZIndex(9));
        renderer.getRoot().addComponent(
                new TextLabel(renderer, AABB.newAABB(0, 640, 200, 704), font, "Never gonna say goodbye", 0x000F000F)
                        .setZIndex(10));
        renderer.getRoot().addComponent(new TextLabel(renderer, AABB.newAABB(0, 704, 200, 768), font,
                "Never gonna tell a lie and hurt you", 0x0000F0F0).setZIndex(11));
        while (!window.shouldClose()) {
            window.clear();
            long current = System.currentTimeMillis() / 8;
            tex.activeAndBind(0);
            array2.getVBO().updateVertexPos(0, nowX(-1, -1, current), nowY(-1, -1, current), 0);
            array2.getVBO().updateVertexPos(1, nowX(1, -1, current), nowY(1, -1, current), 0);
            array2.getVBO().updateVertexPos(2, nowX(1, 1, current), nowY(1, 1, current), 0);
            array2.getVBO().updateVertexPos(3, nowX(-1, 1, current), nowY(-1, 1, current), 0);
            array2.getVBO().updateVertexUV(0, nowX(0, 0, current), nowY(0, 0, current));
            array2.getVBO().updateVertexUV(1, nowX(0, 1, current), nowY(0, 1, current));
            array2.getVBO().updateVertexUV(2, nowX(1, 0, current), nowY(1, 0, current));
            array2.getVBO().updateVertexUV(3, nowX(1, 1, current), nowY(1, 1, current));
            array2.render();

            renderer.render();

            window.updateDisplay(false);
            window.limitDisplayFPS();
        }
        try {
            font.getCodepointInfo('W').atlas.getImage().writeToFile("D:\\test.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        window.close();
        SoundSystem.stop();
    }

    public float nowX(float x, float y, long time) {
        float sin = (float) Math.sin(Math.toRadians(time));
        float cos = (float) Math.cos(Math.toRadians(time));
        return cos * x - sin * y;
    }

    public float nowY(float x, float y, long time) {
        float sin = (float) Math.sin(Math.toRadians(time));
        float cos = (float) Math.cos(Math.toRadians(time));
        return sin * x + cos * y;
    }

    public Renderer getRenderer() {
        return renderer;
    }
}
