package com.bn.Sample9_1;

import android.opengl.GLES20;

import com.yxf.opengl.common.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//圆柱侧面
public class CylinderSide {
    int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用
    int maTexCoorHandle; //顶点纹理坐标属性引用

    int muMMatrixHandle;//位置、旋转、缩放变换矩阵
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

    public CylinderSide(MySurfaceView mv, float scale, float r, float h, int n) {
        //调用初始化顶点数据的initVertexData方法
        initVertexData(scale, r, h, n);
        //调用初始化着色器的intShader方法
        initShader(mv);
    }

    //自定义初始化顶点坐标数据的方法
    public void initVertexData(
            float scale,    //大小
            float r,        //半径
            float h,        //高度
            int n            //切分的份数
    ) {
        r = scale * r;
        h = scale * h;

        float angdegSpan = 360.0f / n;
        vCount = 3 * n * 4;//顶点个数，共有3*n*4个三角形，每个三角形都有三个顶点
        //坐标数据初始化
        float[] vertices = new float[vCount * 3];
        float[] textures = new float[vCount * 2];//顶点纹理S、T坐标值数组
        //坐标数据初始化
        int count = 0;
        int stCount = 0;
        for (float angdeg = 0; Math.ceil(angdeg) < 360; angdeg += angdegSpan)//侧面
        {
            double angrad = Math.toRadians(angdeg);//当前弧度
            double angradNext = Math.toRadians(angdeg + angdegSpan);//下一弧度
            //底圆当前点---0
            vertices[count++] = (float) (-r * Math.sin(angrad));
            vertices[count++] = 0;
            vertices[count++] = (float) (-r * Math.cos(angrad));

            textures[stCount++] = (float) (angrad / (2 * Math.PI));//st坐标
            textures[stCount++] = 1;
            //顶圆下一点---3
            vertices[count++] = (float) (-r * Math.sin(angradNext));
            vertices[count++] = h;
            vertices[count++] = (float) (-r * Math.cos(angradNext));

            textures[stCount++] = (float) (angradNext / (2 * Math.PI));//st坐标
            textures[stCount++] = 0;
            //顶圆当前点---2
            vertices[count++] = (float) (-r * Math.sin(angrad));
            vertices[count++] = h;
            vertices[count++] = (float) (-r * Math.cos(angrad));

            textures[stCount++] = (float) (angrad / (2 * Math.PI));//st坐标
            textures[stCount++] = 0;

            //底圆当前点---0
            vertices[count++] = (float) (-r * Math.sin(angrad));
            vertices[count++] = 0;
            vertices[count++] = (float) (-r * Math.cos(angrad));

            textures[stCount++] = (float) (angrad / (2 * Math.PI));//st坐标
            textures[stCount++] = 1;
            //底圆下一点---1
            vertices[count++] = (float) (-r * Math.sin(angradNext));
            vertices[count++] = 0;
            vertices[count++] = (float) (-r * Math.cos(angradNext));

            textures[stCount++] = (float) (angradNext / (2 * Math.PI));//st坐标
            textures[stCount++] = 1;
            //顶圆下一点---3
            vertices[count++] = (float) (-r * Math.sin(angradNext));
            vertices[count++] = h;
            vertices[count++] = (float) (-r * Math.cos(angradNext));

            textures[stCount++] = (float) (angradNext / (2 * Math.PI));//st坐标
            textures[stCount++] = 0;
        }
        //法向量数据初始化
        float[] normals = new float[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            if (i % 3 == 1) {
                normals[i] = 0;
            } else {
                normals[i] = vertices[i];
            }
        }


        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);//创建顶点坐标数据缓冲
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置

        ByteBuffer nbb = ByteBuffer.allocateDirect(vertices.length * 4);//创建顶点法向量数据缓冲
        nbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mNormalBuffer = nbb.asFloatBuffer();//转换为float型缓冲
        mNormalBuffer.put(normals);//向缓冲区中放入顶点法向量数据
        mNormalBuffer.position(0);//设置缓冲区起始位置

        //st坐标数据初始化
        ByteBuffer cbb = ByteBuffer.allocateDirect(textures.length * 4);//创建顶点纹理数据缓冲
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mTexCoorBuffer = cbb.asFloatBuffer();//转换为float型缓冲
        mTexCoorBuffer.put(textures);//向缓冲区中放入顶点纹理数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
    }

    //自定义初始化着色器的initShader方法
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
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
    }
}
