#version 300 es

in vec2 aTexCoord;
in vec3 aPos;
out vec2 vTexCoord;
out vec4 vPos;
uniform mat4 uProj, uView, uModel;

void main() {
    gl_Position = uProj * uView * uModel * vec4(aPos, 1.);
    vTexCoord = aTexCoord;
    vPos = uModel * vec4(aPos, 1.);
}