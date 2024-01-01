#version 310 es

in vec2 aPos;
out vec2 vPos;
uniform mat4 uProj, uView, uModel;
uniform float uHeight;

void main() {
    gl_Position = uProj * uView * uModel * vec4(aPos.x, uHeight, aPos.y, 1.0);
    vPos = aPos;
}