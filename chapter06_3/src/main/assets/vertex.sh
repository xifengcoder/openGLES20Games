uniform mat4 uMVPMatrix; 						//总变换矩阵
uniform mat4 uMMatrix; 							//变换矩阵(包括平移、旋转、缩放)
uniform vec3 uLightLocation;						//定位光源位置
attribute vec3 aPosition;  						//顶点位置
attribute vec3 aNormal;    						//顶点法向量
varying vec3 vPosition;							//用于传递给片元着色器的顶点位置
varying vec4 vDiffuse;							//用于传递给片元着色器的散射光分量

void main(){
   vec4 lightDiffuse = vec4(0.8,0.8,0.8,1.0); //散射光强度
   //计算顶点在世界空间中的位置
   vec3 fragPos = vec3(uMMatrix * vec4(aPosition, 1));
   //计算光源和片元位置之间的方向向量
   vDiffuse = lightDiffuse * max(0.0, dot(normalize(aNormal), normalize(uLightLocation - fragPos))); //计算散射光的最终强度
   vPosition = aPosition; //将顶点的位置传给片元着色器
   gl_Position = uMVPMatrix * vec4(aPosition,1); //根据总变换矩阵计算此次绘制此顶点的位置
}

