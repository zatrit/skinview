#version 300 es

precision highp float;

in vec2 vTexCoord;
in vec4 vPos;
out vec4 oFragColor;
uniform bool uLight;
uniform sampler2D uTexture;

float calcLight() {
    const float amb = .1;
    // light source
    const vec3 L = vec3(0., 0., 1.);

    // https://stackoverflow.com/a/53446396
    vec3 n_pos = vPos.xyz / vPos.w;
    vec3 dx = dFdx(n_pos);
    vec3 dy = dFdy(n_pos);

    vec3 N = normalize(cross(dx, dy));
    N *= sign(N.z);
    float diff = dot(N, L);

    return min(amb + diff, 1.);
}

void main() {
    vec4 col = texture(uTexture, vTexCoord);

    if (col.a > .0) {
        float l = uLight ? calcLight() : 1.;
        oFragColor = vec4(col.rgb * l, 1.);
    }
    else discard;
}