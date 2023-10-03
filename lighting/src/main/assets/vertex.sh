uniform mat4 uMVPMatrix; //总变换矩阵
uniform mat4 uMMatrix; //变换矩阵(包括平移、旋转、缩放)
uniform vec3 uLightLocation; //定位光源位置
uniform vec3 uCamera;        //摄像机位置

attribute vec3 aPosition;  //顶点位置
attribute vec3 aNormal;  //顶点法向量

varying vec3 vPosition;//用于传递给片元着色器的顶点位置
varying vec4 vAmbient; //用于传递给片元着色器的环境光最终强度
varying vec4 vDiffuse; //用于传递给片元着色器的散射光最终强度
varying vec4 vSpecular; //用于传递给片元着色器的散射光分量


void pointLight(                      //定位光光照计算的方法
    	inout vec4 lightAmbient,			//环境光最终强度
        inout vec4 lightDiffuse,		//散射光最终强度
        inout vec4 lightSpecular,			//镜面光最终强度
        in vec3 lightLocation,			  //光源位置
        in vec4 ambientFactor,			  //环境光强度
        in vec4 diffuseFactor,			  //散射光强度
        in vec4 specularFactor			  //镜面光强度
){
    lightAmbient = ambientFactor;			//直接得出环境光的最终强度
    vec4 fragPos = uMMatrix * vec4(aPosition, 1);
    vec3 modifiedNormal = aPosition + aNormal;
    vec4 normalPos =  uMMatrix * vec4(modifiedNormal, 1);
    vec3 normalVector = normalize(normalPos.xyz - fragPos.xyz); //法向量

    vec3 eyeDirection= normalize(uCamera - fragPos.xyz);  //计算从表面点到摄像机的向量
    //计算从表面点到光源位置的向量vp
    vec3 lightDirection = normalize(lightLocation - fragPos.xyz);
    vec3 halfVector = normalize(lightDirection + eyeDirection);	//求视线与光线的半向量
    lightDiffuse = diffuseFactor * max(0.0, dot(normalVector, lightDirection));    //计算散射光的最终强度
    float specularStrength = max(0.0, pow(dot(normalVector, halfVector), 64.0)); 	//镜面反射光强度因子
    lightSpecular = specularFactor * specularStrength;    			//计算镜面光的最终强度
}

void main(){
    vec4 lightAmbient, lightDiffuse, lightSpecular;	  //用来接收三个通道最终强度的变量
    pointLight(lightAmbient, lightDiffuse, lightSpecular, uLightLocation,
    		vec4(0.15,0.15,0.15,1.0), vec4(0.8,0.8,0.8,1.0), vec4(0.7,0.7,0.7,1.0));
	vAmbient = lightAmbient; 		//将环境光最终强度传给片元着色器
	vDiffuse = lightDiffuse; 		//将散射光最终强度传给片元着色器
	vSpecular = lightSpecular; 		//将镜面光最终强度传给片元着色器
	vPosition = aPosition;  //将顶点的位置传给片元着色器
	gl_Position = uMVPMatrix * vec4(aPosition, 1); //根据总变换矩阵计算此次绘制此顶点的位置
}