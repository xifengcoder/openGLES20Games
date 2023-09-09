attribute vec3 aPosition;
attribute vec4 aColor;
uniform mat4 uMVPMatrix; //总变换矩阵
varying vec4 vColor;

void main()
{
    vColor = aColor;
    gl_Position = uMVPMatrix * vec4(aPosition,1); //根据总变换矩阵计算此次绘制此顶点位置
}