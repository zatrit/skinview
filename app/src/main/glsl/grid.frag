#version 300 es

precision highp float;

in vec2 vPos;
out vec4 oFragColor;
const vec4 cColor = vec4(1, 1, 1, .5);

void main() {
    const float s = .025, s2 = .05;
    if (mod(vPos.x + s, .5) < s2 || mod(vPos.y + s, .5) < s2) oFragColor = cColor;
}