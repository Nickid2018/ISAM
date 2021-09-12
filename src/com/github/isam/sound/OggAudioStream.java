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

import com.google.common.collect.Lists;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

public class OggAudioStream implements Closeable {

    private final AudioFormat audioFormat;
    private final InputStream input;
    private long handle;
    private ByteBuffer buffer = MemoryUtil.memAlloc(8192);

    public OggAudioStream(InputStream stream) throws IOException {
        input = stream;
        buffer.limit(0);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer dataBlocks = stack.mallocInt(1);
            IntBuffer error = stack.mallocInt(1);
            while (handle == 0L) {
                if (!refillFromStream())
                    throw new IOException("Failed to find Ogg header");
                int pos = buffer.position();
                buffer.position(0);
                handle = STBVorbis.stb_vorbis_open_pushdata(buffer, dataBlocks, error, null);
                buffer.position(pos);
                int errorInt = error.get(0);
                if (errorInt == 1) {
                    forwardBuffer();
                    continue;
                }
                if (errorInt != 0)
                    throw new IOException("Failed to read Ogg file " + errorInt);
            }
            buffer.position(buffer.position() + dataBlocks.get(0));
            STBVorbisInfo info = STBVorbisInfo.mallocStack(stack);
            STBVorbis.stb_vorbis_get_info(handle, info);
            audioFormat = new AudioFormat(info.sample_rate(), 16, info.channels(), true, false);
        }
    }

    private static int clamp(int value) {
        return value > 32767 ? 32767 : (Math.max(value, -32768));
    }

    private boolean refillFromStream() throws IOException {
        int limit = buffer.limit();
        int remains = buffer.capacity() - limit;
        if (remains == 0)
            return true;
        byte[] streamFill = new byte[remains];
        int length = input.read(streamFill);
        if (length == -1)
            return false;
        int pos = buffer.position();
        buffer.limit(limit + length);
        buffer.position(limit);
        buffer.put(streamFill, 0, length);
        buffer.position(pos);
        return true;
    }

    private void forwardBuffer() {
        boolean atHead = (buffer.position() == 0);
        boolean atTail = (buffer.position() == buffer.limit());
        if (atTail && !atHead) {
            buffer.position(0);
            buffer.limit(0);
        } else {
            ByteBuffer newBuffer = MemoryUtil.memAlloc(atHead ? (2 * buffer.capacity()) : buffer.capacity());
            newBuffer.put(buffer);
            MemoryUtil.memFree(buffer);
            newBuffer.flip();
            buffer = newBuffer;
        }
    }

    private boolean readFrame(ChannelList list) throws IOException {
        if (handle == 0L)
            return false;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer output = stack.mallocPointer(1);
            IntBuffer channels = stack.mallocInt(1);
            IntBuffer sample = stack.mallocInt(1);
            while (true) {
                int i = STBVorbis.stb_vorbis_decode_frame_pushdata(handle, buffer, channels, output, sample);
                buffer.position(buffer.position() + i);
                int errorCode = STBVorbis.stb_vorbis_get_error(handle);
                if (errorCode == 1) {
                    forwardBuffer();
                    if (!refillFromStream())
                        break;
                    continue;
                }
                if (errorCode != 0)
                    throw new IOException("Failed to read Ogg file " + errorCode);
                int sampleCount = sample.get(0);
                if (sampleCount == 0)
                    continue;
                int channelCount = channels.get(0);
                PointerBuffer pointer = output.getPointerBuffer(channelCount);
                if (channelCount == 1) {
                    convertMono(pointer.getFloatBuffer(0, sampleCount), list);
                    return true;
                }
                if (channelCount == 2) {
                    convertStereo(pointer.getFloatBuffer(0, sampleCount), pointer.getFloatBuffer(1, sampleCount), list);
                    return true;
                }
                throw new IllegalStateException("Invalid number of channels: " + channelCount);
            }
            return false;
        }
    }

    private void convertMono(FloatBuffer channel, ChannelList list) {
        while (channel.hasRemaining())
            list.put(channel.get());
    }

    private void convertStereo(FloatBuffer channel1, FloatBuffer channel2, ChannelList list) {
        while (channel1.hasRemaining() && channel2.hasRemaining()) {
            list.put(channel1.get());
            list.put(channel2.get());
        }
    }

    public void close() throws IOException {
        if (handle != 0L) {
            STBVorbis.stb_vorbis_close(handle);
            handle = 0L;
        }
        MemoryUtil.memFree(buffer);
        input.close();
    }

    public AudioFormat getFormat() {
        return audioFormat;
    }

    @Nullable
    public ByteBuffer read(int channels) throws IOException {
        ChannelList list = new ChannelList(channels + 8192);
        while (readFrame(list) && list.byteCount < channels)
            ;
        return list.get();
    }

    public ByteBuffer readAll() throws IOException {
        ChannelList list = new ChannelList(16384);
        while (readFrame(list))
            ;
        return list.get();
    }

    static class ChannelList {

        private final List<ByteBuffer> buffers = Lists.newArrayList();

        private final int bufferSize;

        private int byteCount;

        private ByteBuffer currentBuffer;

        public ChannelList(int size) {
            bufferSize = size + 1 & 0xFFFFFFFE;
            createNewBuffer();
        }

        private void createNewBuffer() {
            currentBuffer = BufferUtils.createByteBuffer(bufferSize);
        }

        public void put(float f) {
            if (currentBuffer.remaining() == 0) {
                currentBuffer.flip();
                buffers.add(currentBuffer);
                createNewBuffer();
            }
            int i = clamp((int) (f * 32767.5F - 0.5F));
            currentBuffer.putShort((short) i);
            byteCount += 2;
        }

        public ByteBuffer get() {
            currentBuffer.flip();
            if (buffers.isEmpty())
                return currentBuffer;
            ByteBuffer byteBuffer = BufferUtils.createByteBuffer(byteCount);
            buffers.forEach(byteBuffer::put);
            byteBuffer.put(currentBuffer);
            byteBuffer.flip();
            return byteBuffer;
        }
    }
}
