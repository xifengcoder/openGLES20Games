//地球着色器
precision mediump float;
varying vec2 vTextureCoord;//接收从顶点着色器过来的参数
uniform sampler2D sTextureDay;//纹理内容数据
void main()
{
  //给此片元从纹理中采样出颜色值
  vec4 finalColorDay= texture2D(sTextureDay, vTextureCoord);
  gl_FragColor = finalColorDay;
}