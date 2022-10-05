package com.bn.Sample5_7;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//颜色条状物
public class Belt {
    private static final int N = 6;
    private static int VERTEX_COUNT = 2 * (N + 1);

    int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用
    int maColorHandle; //顶点颜色属性引用
    String mVertexShader;//顶点着色器代码脚本
    String mFragmentShader;//片元着色器代码脚本

    FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    FloatBuffer mColorBuffer;//顶点着色数据缓冲

    public Belt(MySurfaceView mv) {
        //初始化顶点坐标与着色数据
        initVertexData();
        //初始化shader
        initShader(mv);
    }

    //初始化顶点坐标与着色数据的方法
    public void initVertexData() {
        float angdegBegin = -90;
        float angdegEnd = 90;
        float angdegSpan = (angdegEnd - angdegBegin) / N;

        float[] vertices = new float[VERTEX_COUNT * 3];//坐标数据
        //坐标数据初始化
        int count = 0;
        for (float angdeg = angdegBegin; angdeg <= angdegEnd; angdeg += angdegSpan) {
            double angrad = Math.toRadians(angdeg);//当前弧度
            //当前点
            vertices[count++] = (float) (-0.6f * Constant.UNIT_SIZE * Math.sin(angrad));//顶点坐标
            vertices[count++] = (float) (0.6f * Constant.UNIT_SIZE * Math.cos(angrad));
            vertices[count++] = 0;
            //当前点
            vertices[count++] = (float) (-Constant.UNIT_SIZE * Math.sin(angrad));//顶点坐标
            vertices[count++] = (float) (Constant.UNIT_SIZE * Math.cos(angrad));
            vertices[count++] = 0;
        }

        //创建顶点坐标数据缓冲
        mVertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertices);

        mVertexBuffer.position(0);//设置缓冲区起始位置

        //顶点颜色值数组，每个顶点4个色彩值RGBA
        count = 0;
        float colors[] = new float[VERTEX_COUNT * 4];
        for (int i = 0; i < colors.length; i += 8) {
            colors[count++] = 1;
            colors[count++] = 1;
            colors[count++] = 1;
            colors[count++] = 0;

            colors[count++] = 1;
            colors[count++] = 1;
            colors[count++] = 0;
            colors[count++] = 0;
        }

        //创建顶点着色数据缓冲
        mColorBuffer = ByteBuffer.allocateDirect(colors.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(colors);
        mColorBuffer.position(0);//设置缓冲区起始位置
    }

    //初始化shader
    public void initShader(MySurfaceView mv) {
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        maColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void drawSelf() {
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        GLES20.glVertexAttribPointer(maColorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, mColorBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maColorHandle);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VERTEX_COUNT);
    }
}