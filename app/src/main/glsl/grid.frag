#version 310 es

precision highp float;

in vec2 vPos;
out vec4 oFragColor;
uniform vec4 uColor;

void main() {
    const float s = .025, s2 = .05;
    if (mod(vPos.x + s, .5) < s2 || mod(vPos.y + s, .5) < s2) oFragColor = uColor;
}