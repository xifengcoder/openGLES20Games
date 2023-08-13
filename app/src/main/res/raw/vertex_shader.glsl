attribute vec3 position;
attribute vec3 normal;
attribute vec2 inputTextureCoordinate;
varying vec3 faceNormal;
varying vec2 textureCoordinate;

uniform mat4 uMVPMatrix; //总变换矩阵

void main()
{
    faceNormal = normal;
    textureCoordinate = inputTextureCoordinate;
    gl_Position = uMVPMatrix * vec4(position,1); //根据总变换矩阵计算此次绘制此顶点位置
}