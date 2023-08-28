package com.bn.Sample9_4;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

//加载后的物体——仅携带顶点信息，颜色随机
public class LoadedObjectVertexNormalTexture {
    int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int muMMatrixHandle;//位置、旋转变换矩阵
    int maPositionHandle; //顶点位置属性引用  
    int maNormalHandle; //顶点法向量属性引用  
    int maLightLocationHandle;//光源位置属性引用  
    int maCameraHandle; //摄像机位置属性引用 
    int maTexCoorHandle; //顶点纹理坐标属性引用  
    String mVertexShader;//顶点着色器代码脚本    	 
    String mFragmentShader;//片元着色器代码脚本    

    FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    FloatBuffer mNormalBuffer;//顶点法向量数据缓冲
    FloatBuffer mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int vCount = 0;

    public LoadedObjectVertexNormalTexture(MySurfaceView mv, float[] vertices, float[] normals, float texCoors[]) {
        //初始化顶点坐标与着色数据
        initVertexData(vertices, normals, texCoors);
        //初始化shader
        initShader(mv);
    }

    //初始化顶点坐标与着色数据的方法
    public void initVertexData(float[] vertices, float[] normals, float texCoors[]) {
        //顶点坐标数据的初始化================begin============================
        vCount = vertices.length / 3;

        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        mVertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(vertices);
        mVertexBuffer.position(0);//设置缓冲区起始位置

        //顶点法向量数据的初始化================begin============================  
        mNormalBuffer = ByteBuffer.allocateDirect(normals.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(normals);
        mNormalBuffer.position(0);//设置缓冲区起始位置

        //顶点纹理坐标数据的初始化================begin============================  
        mTexCoorBuffer = ByteBuffer.allocateDirect(texCoors.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(texCoors);
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
    }

    //初始化shader
    public void initShader(MySurfaceView mv) {
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        maNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");
        maLightLocationHandle = GLES20.glGetUniformLocation(mProgram, "uLightLocation");
        maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        maCameraHandle = GLES20.glGetUniformLocation(mProgram, "uCamera");
    }

    public void drawSelf(int texId) {
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
        GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
        // 将顶点位置数据传入渲染管线
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        //将顶点法向量数据传入渲染管线
        GLES20.glVertexAttribPointer(maNormalHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mNormalBuffer);
        //为画笔指定顶点纹理坐标数据
        GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, mTexCoorBuffer);
        //启用顶点位置、法向量、纹理坐标数据
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maNormalHandle);
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);
        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
        //绘制加载的物体
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
    }
}
