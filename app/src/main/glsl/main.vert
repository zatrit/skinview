#version 300 es

layout(location = 1) in vec2 aTexCoord;
layout(location = 0) in vec3 aPos;
out vec2 vTexCoord;
out vec4 vPos;
uniform mat4 uProj, uView, uModel;

void main() {
    vPos = uView * uModel * vec4(aPos, 1);
    gl_Position = uProj * vPos;
    vTexCoord = aTexCoord;
}
