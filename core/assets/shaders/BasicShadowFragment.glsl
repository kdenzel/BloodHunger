#version 120

#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_shadowPos;
varying vec2 v_position;
varying float v_distance;

uniform sampler2D u_texture;

void main() {
    float dist = 0;
    dist = pow(abs(v_shadowPos.x - v_position.x),2);
    dist += pow(abs(v_shadowPos.y - v_position.y), 2);
    dist = sqrt(dist);

    vec4 texture2D = texture2D(u_texture,v_texCoords);
    gl_FragColor = texture2D / max(1,v_distance / max(0.00000001,dist));
}