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
package com.github.isam.render.shader;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;

public class ShaderProgram {

    private final int id;
    private final Map<String, Uniform> uniforms;

    public ShaderProgram(String vertex, String frag, Uniform... uniforms) {
        int vsId = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vsId, vertex);
        glCompileShader(vsId);
        if (glGetShaderi(vsId, GL_COMPILE_STATUS) == 0)
            throw new RuntimeException("Can't compile vertex shader: " + glGetShaderInfoLog(vsId));
        int fsId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fsId, frag);
        glCompileShader(fsId);
        if (glGetShaderi(fsId, GL_COMPILE_STATUS) == 0)
            throw new RuntimeException("Can't compile fragment shader: " + glGetShaderInfoLog(fsId));
        id = glCreateProgram();
        glAttachShader(id, vsId);
        glAttachShader(id, fsId);
        glLinkProgram(id);
        if (glGetProgrami(id, GL_LINK_STATUS) == 0)
            throw new RuntimeException("Can't link shader program: " + glGetProgramInfoLog(id));
        glValidateProgram(id);
        if (glGetProgrami(id, GL_VALIDATE_STATUS) == 0)
            throw new RuntimeException("Can't validate shader program: " + glGetProgramInfoLog(id));
        glDeleteShader(vsId);
        glDeleteShader(fsId);
        this.uniforms = Maps.newHashMap();
        for (Uniform uniform : uniforms) {
            this.uniforms.put(uniform.getName(), uniform);
            uniform.updateUniformLocation(id);
        }
    }

    public static ShaderProgram createFromJAR(String vs, String fs, Uniform... uniforms) throws IOException {
        String vss = IOUtils.resourceToString(vs, Charsets.UTF_8);
        String fss = IOUtils.resourceToString(fs, Charsets.UTF_8);
        return new ShaderProgram(vss, fss, uniforms);
    }

    public void use() {
        glUseProgram(id);
        uniforms.values().forEach(Uniform::upload);
    }

    public void destroy() {
        glDeleteProgram(id);
    }

    public Uniform getUniform(String name) {
        return uniforms.get(name);
    }
}
