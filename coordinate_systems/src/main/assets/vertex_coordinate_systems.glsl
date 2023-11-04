attribute vec3 aPosition;
attribute in vec2 aTexCoord;
uniform mat4 uMVPMatrix;
varying vec2 vTexCoord;

void main() {
    gl_Position = uMVPMatrix * vec4(aPosition, 1);
    vTexCoord = vec2(aTexCoord.x, 1.0 - aTexCoord.y);
}
