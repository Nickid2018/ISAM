#version 330 core

in vec3 outColor;
in vec2 outUV;

out vec4 FragColor;

uniform sampler2D sampler;

void main()
{
	FragColor = vec4(outColor, texture(sampler, outUV));
}