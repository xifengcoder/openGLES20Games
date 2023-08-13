precision mediump float;
varying highp vec3 faceNormal;
varying highp vec2 textureCoordinate;
uniform sampler2D inputImageTexture1;
void main()
{
    gl_FragColor = texture2D(inputImageTexture1, textureCoordinate);
}