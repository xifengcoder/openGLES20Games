precision mediump float;
varying vec2 vTexCoord;

uniform sampler2D texture1;
uniform sampler2D texture2;

void main() {
    gl_fragcolor = mix(texture(texture1, vTexCoord), texture(texture2, vTexCoord), 0.2);
}