#version 300 es

precision highp float;

in vec2 vTexCoord;
in vec4 vPos;
out vec4 oFragColor;
uniform bool uShade;
uniform sampler2D uTexture;

float calcLight() {
    // based on:
    // https://learnopengl.com/Lighting/Basic-Lighting
    // https://stackoverflow.com/a/53446396

    // ambient lighting
    const float amb = .2;

    // diffuse lighting
    const vec3 L = vec3(0, 0, 1);// light source
    vec3 nPos = vPos.xyz / vPos.w;
    vec3 dx = dFdx(nPos), dy = dFdy(nPos);

    vec3 N = normalize(cross(dx, dy));
    N *= sign(N.z);
    float diff = dot(N, L);

    return min(amb + diff, 1.);
}

void main() {
    vec4 col = texture(uTexture, vTexCoord);

    if (col.a > .0) {
        float l = uShade ? calcLight() : 1.;
        oFragColor = vec4(col.rgb * l, 1);
    }
    else discard;
}