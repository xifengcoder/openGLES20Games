uniform mat4 uMVPMatrix; //总变换矩阵
attribute vec3 aPosition;  //顶点位置
varying vec3 vPosition;//用于传递给片元着色器的顶点位置
varying vec4 vAmbient;//用于传递给片元着色器的环境光分量
void main()
{
   //根据总变换矩阵计算此次绘制此顶点位置
   gl_Position = uMVPMatrix * vec4(aPosition,1);
   //将顶点的位置传给片元着色器
   vPosition = aPosition;
   //将的环境光分量传给片元着色器
   vAmbient = vec4(0.15,0.15,0.15,1.0);
}