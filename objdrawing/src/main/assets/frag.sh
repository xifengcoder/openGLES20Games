precision mediump float;
uniform sampler2D sTexture;//纹理内容数据

varying vec4 vAmbient;
varying vec4 vDiffuse;
varying vec4 vSpecular;
varying vec2 vTextureCoord;

void main()
{
    //将计算出的颜色给此片元
    vec4 finalColor=texture2D(sTexture, vTextureCoord);
   gl_FragColor = finalColor * vAmbient + finalColor * vSpecular + finalColor * vDiffuse;//给此片元颜色值
}