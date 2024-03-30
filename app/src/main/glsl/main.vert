#version 300 es

in vec2 aTexCoord;
in vec3 aPos;
out vec2 vTexCoord;
out vec4 vPos;
uniform mat4 uProj, uView, uModel;

void main() {
    vPos = uModel * uView * vec4(aPos, 1);
    gl_Position = uProj * vPos;
    vTexCoord = aTexCoord;
}