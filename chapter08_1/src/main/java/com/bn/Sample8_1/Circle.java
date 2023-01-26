package com.bn.Sample8_1;

import android.opengl.GLES20;

import com.yxf.opengl.common.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//圆面
public class Circle {
    int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用
    int maTexCoorHandle; //顶点纹理坐标属性引用
    int muMMatrixHandle;

    int maCameraHandle; //摄像机位置属性引用
    int maNormalHandle; //顶点法向量属性引用
    int maLightLocationHandle;//光源位置属性引用


    String mVertexShader;//顶点着色器代码脚本
    String mFragmentShader;//片元着色器代码脚本

    FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    FloatBuffer mTexCoorBuffer;//顶点纹理坐标数据缓冲
    FloatBuffer mNormalBuffer;//顶点法向量数据缓冲
    int vCount = 0;
    float xAngle = 0;//绕x轴旋转的角度
    float yAngle = 0;//绕y轴旋转的角度
    float zAngle = 0;//绕z轴旋转的角度

    public Circle(MySurfaceView mv, float scale, float radius, int n) {
        initVertexData(scale, radius, n);
        initShader(mv);
    }

    //自定义的初始化顶点数据的方法

    /**
     * @param scale  //大小
     * @param radius //半径
     * @param n      //切分的份数
     */
    public void initVertexData(float scale, float radius, int n) {
        radius = radius * scale;
        float angleSpan = 360.0f / n;    //顶角的度数
        vCount = 3 * n; //顶点个数，共有n个三角形，每个三角形都有三个顶点

        float[] vertices = new float[vCount * 3];//坐标数据
        float[] textures = new float[vCount * 2];//顶点纹理S、T坐标值数组
        //坐标数据初始化
        int count = 0;
        int stCount = 0;
        for (float angdeg = 0; Math.ceil(angdeg) < 360; angdeg += angleSpan) {
            double curRadian = Math.toRadians(angdeg);//当前弧度
            double nextRadian = Math.toRadians(angdeg + angleSpan);//下一弧度
            //中心点
            vertices[count++] = 0;//顶点坐标
            vertices[count++] = 0;
            vertices[count++] = 0;
            //当前点
            vertices[count++] = (float) (-radius * Math.sin(curRadian));//顶点坐标
            vertices[count++] = (float) (radius * Math.cos(curRadian));
            vertices[count++] = 0;
            //下一点
            vertices[count++] = (float) (-radius * Math.sin(nextRadian));//顶点坐标
            vertices[count++] = (float) (radius * Math.cos(nextRadian));
            vertices[count++] = 0;

            textures[stCount++] = 0.5f; //st坐标
            textures[stCount++] = 0.5f;
            textures[stCount++] = (float) (0.5f - 0.5f * Math.sin(curRadian));//st坐标
            textures[stCount++] = (float) (0.5f - 0.5f * Math.cos(curRadian));
            textures[stCount++] = (float) (0.5f - 0.5f * Math.sin(nextRadian));//st坐标
            textures[stCount++] = (float) (0.5f - 0.5f * Math.cos(nextRadian));
        }

        //法向量数据初始化
        float[] normals = new float[vertices.length];
        for (int i = 0; i < normals.length; i += 3) {
            normals[i] = 0;
            normals[i + 1] = 0;
            normals[i + 2] = 1;
        }

        mVertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertices);
        mVertexBuffer.position(0);

        mNormalBuffer = ByteBuffer.allocateDirect(normals.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(normals);
        mNormalBuffer.position(0);

        mTexCoorBuffer = ByteBuffer.allocateDirect(textures.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textures);
        mTexCoorBuffer.position(0);
    }

    //自定义初始化着色器initShader方法
    public void initShader(MySurfaceView mv) {
        //加载顶点着色器的脚本内容
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex_tex_light.sh", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag_tex_light.sh", mv.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用id
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用id
        maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取程序中顶点法向量属性引用id
        maNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
        //获取程序中摄像机位置引用id
        maCameraHandle = GLES20.glGetUniformLocation(mProgram, "uCamera");
        //获取程序中光源位置引用id
        maLightLocationHandle = GLES20.glGetUniformLocation(mProgram, "uLightLocation");
        //获取位置、旋转变换矩阵引用id
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");
    }

    public void drawSelf(int texId) {
        //制定使用某套shader程序
        GLES20.glUseProgram(mProgram);

        //将最终变换矩阵传入shader程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);

        //将位置、旋转变换矩阵传入shader程序
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        //将摄像机位置传入shader程序
        GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
        //将光源位置传入shader程序
        GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);


        //传送顶点位置数据
        GLES20.glVertexAttribPointer
                (
                        maPositionHandle,
                        3,
                        GLES20.GL_FLOAT,
                        false,
                        3 * 4,
                        mVertexBuffer
                );
        //传送顶点纹理坐标数据
        GLES20.glVertexAttribPointer
                (
                        maTexCoorHandle,
                        2,
                        GLES20.GL_FLOAT,
                        false,
                        2 * 4,
                        mTexCoorBuffer
                );
        //传送顶点法向量数据
        GLES20.glVertexAttribPointer
                (
                        maNormalHandle,
                        4,
                        GLES20.GL_FLOAT,
                        false,
                        3 * 4,
                        mNormalBuffer
                );

        //启用顶点位置数据
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        //启用顶点纹理数据
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);
        //启用顶点法向量数据
        GLES20.glEnableVertexAttribArray(maNormalHandle);


        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);

        //绘制纹理矩形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vCount);
    }
}
