#version 120

attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_worldView;
uniform vec2 u_shadowPos;
uniform float u_distance;

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_shadowPos;
varying vec2 v_position;
varying float v_distance;

void main() {
    v_color = a_color;
    v_position.xy = a_position.xy;
    v_shadowPos = u_shadowPos;
    v_texCoords = a_texCoord0;
    v_distance = u_distance;
    gl_Position = u_worldView * a_position;
}