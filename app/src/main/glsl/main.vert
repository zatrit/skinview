#version 310 es

in vec3 aPos;
in vec2 aTexCoord;
out vec2 vTexCoord;
uniform mat4 uProj, uView, uModel;

void main() {
    gl_Position = uProj * uView * uModel * vec4(aPos, 1.0);
    vTexCoord = aTexCoord;
}