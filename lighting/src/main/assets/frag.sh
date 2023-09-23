precision mediump float; //指定浮点相关变量的精度
uniform float uRadius; //从宿主程序中传入的球半径
varying vec2 mcLongLat;//接收从顶点着色器过来的参数
varying vec3 vPosition;//接收从顶点着色器过来的顶点位置
varying vec4 vAmbient;//接收从顶点着色器过来的环境光分量

void main()
{
   vec3 color;
   float n = 12.0;//一个坐标分量分的总份数
   float span = 2.0 * uRadius / n;//外接立方体每个坐标轴方向切分的份数
   int i = int((vPosition.x + uRadius) / span);//当前片元位置小方块的行数
   int j = int((vPosition.y + uRadius) / span);//当前片元位置小方块的层数
   int k = int((vPosition.z + uRadius) / span); //当前片元位置小方块的列数
   //计算当前片元行数、层数和列数的和并对2取模
   int whichColor = int(mod(float(i+j+k),2.0));
   if(whichColor == 1) {//奇数时为红色
   		color = vec3(0.678,0.231,0.129);//红色
   } else {//偶数时为白色
   		color = vec3(1.0,1.0,1.0);//白色
   }
   //最终颜色
   vec4 finalColor = vec4(color,0);
   //给此片元颜色值
   gl_FragColor = finalColor * vAmbient;
}