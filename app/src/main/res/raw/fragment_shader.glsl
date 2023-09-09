precision mediump float;
varying vec4 vColor;          	// This is the color from the vertex shader interpolated across the

void main()
{
    gl_FragColor = vColor;
}