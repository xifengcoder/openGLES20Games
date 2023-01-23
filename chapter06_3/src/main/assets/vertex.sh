uniform mat4 uMVPMatrix; 						//总变换矩阵
uniform mat4 uMMatrix; 							//变换矩阵(包括平移、旋转、缩放)
uniform vec3 uLightLocation;						//定位光源位置
attribute vec3 aPosition;  						//顶点位置
attribute vec3 aNormal;    						//顶点法向量
varying vec3 vPosition;							//用于传递给片元着色器的顶点位置
varying vec4 vDiffuse;							//用于传递给片元着色器的散射光分量

void main(){
   gl_Position = uMVPMatrix * vec4(aPosition,1); 	//根据总变换矩阵计算此次绘制此顶点的位置
   vec3 normal = normalize(aNormal); //法向量
   vec4 lightDiffuse = vec4(0.8,0.8,0.8,1.0); //散射光强度
   vec3 normalTarget = aPosition + normal;					//计算变换后的法向量
   vec3 newNormal = normalize((uMMatrix * vec4(normalTarget, 1)).xyz - (uMMatrix * vec4(aPosition, 1)).xyz);
   //计算从表面点到光源位置的向量vp
   vec3 vp = normalize(uLightLocation - (uMMatrix * vec4(aPosition, 1)).xyz);
   vec4 diffUseResult = lightDiffuse * max(0.0, dot(newNormal, vp));			//计算散射光的最终强度
   vDiffuse = diffUseResult;					//将散射光最终强度传给片元着色器
   vPosition = aPosition; 					//将顶点的位置传给片元着色器
}

