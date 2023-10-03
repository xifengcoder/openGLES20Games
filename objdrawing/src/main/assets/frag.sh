precision mediump float;
varying vec4 vAmbient;
varying vec4 vDiffuse;
varying vec4 vSpecular;
void main()
{
   //将计算出的颜色给此片元
   vec4 finalColor = vec4(0.9, 0.9, 0.9, 1.0);
   gl_FragColor = finalColor * vAmbient + finalColor * vSpecular + finalColor * vDiffuse;//给此片元颜色值
}