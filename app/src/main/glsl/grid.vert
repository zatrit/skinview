#version 300 es

in vec2 aPos;
out vec2 vPos;
uniform mat4 uProj, uView, uModel;

void main() {
    gl_Position = uProj * uView * uModel * vec4(aPos.x, -2.01, aPos.y, 1);
    vPos = aPos;
}