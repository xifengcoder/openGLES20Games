uniform mat4 uMVPMatrix; //总变换矩阵
uniform mat4 uMMatrix; //变换矩阵(包括平移、旋转、缩放)
uniform vec3 uLightLocation; //定位光源位置
uniform vec3 uCamera;        //摄像机位置

attribute vec3 aPosition;  //顶点位置
attribute vec3 aNormal;  //顶点法向量

varying vec3 vPosition;//用于传递给片元着色器的顶点位置
varying vec4 vSpecular; //用于传递给片元着色器的散射光分量


void pointLight(                //定位光光照计算的方法
    in vec3 normal,            //法向量
    inout vec4 specular,        //镜面反射光分量
    in vec3 lightLocation,        //光源位置
    in vec4 lightSpecular        //镜面光强度
){
    vec3 normalTarget = aPosition + normal;    //计算变换后的法向量
    vec4 fragPos = uMMatrix * vec4(aPosition,1);
    vec3 modifiedNormal = (uMMatrix * vec4(normalTarget,1)).xyz - fragPos.xyz;
    modifiedNormal = normalize(modifiedNormal);    //对法向量规格化
    //计算从表面点到摄像机的向量
    vec3 viewDir = normalize(uCamera - fragPos.xyz);
    //计算从表面点到光源位置的向量vp
    vec3 lightDir = normalize(lightLocation - fragPos.xyz);
    vec3 halfVector = normalize(lightDir + viewDir);    //求视线与光线的半向量
    float shininess = 50.0;                //粗糙度，越小越光滑
    float nDotViewHalfVector = dot(modifiedNormal, halfVector);            //法线与半向量的点积
    float powerFactor = max(0.0, pow(nDotViewHalfVector, shininess));    //镜面反射光强度因子
    specular = lightSpecular * powerFactor;    //最终的镜面光强度
}

void main(){
    vec4 specular = vec4(0.0,0.0,0.0,0.0);
    pointLight(normalize(aNormal), specular, uLightLocation, vec4(0.7,0.7,0.7,1.0));//计算镜面光
    vSpecular = specular;    //将最终镜面光强度传给片元着色器
    vPosition = aPosition;        //将顶点的位置传给片元着色器
    gl_Position = uMVPMatrix * vec4(aPosition, 1); //根据总变换矩阵计算此次绘制此顶点的位置
}