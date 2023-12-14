#version 310 es

precision highp float;

in vec2 vTexCoord;
out vec4 oFragColor;
uniform sampler2D uTexture;

void main() {
    vec4 col = texture(uTexture, vTexCoord);

    if (col.a > .0) oFragColor = col;
    else discard;
}