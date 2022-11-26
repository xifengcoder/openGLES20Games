package com.bn.Sample5_3;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//颜色立方体
public class Cube {
    int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用
    int maColorHandle; //顶点颜色属性引用
    String mVertexShader;//顶点着色器代码脚本
    String mFragmentShader;//片元着色器代码脚本

    FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    FloatBuffer mColorBuffer;//顶点着色数据缓冲
    int vCount = 0;

    public Cube(MySurfaceView mv) {
        initVertexData();
        initShader(mv);
    }

    public void initVertexData() {
        vCount = 12 * 6;
        float[] vertices = new float[]{
                //前面
                0, 0, Constant.UNIT_SIZE,
                Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                -Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                0, 0, Constant.UNIT_SIZE,
                -Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                0, 0, Constant.UNIT_SIZE,
                -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                0, 0, Constant.UNIT_SIZE,
                Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                //后面
                0, 0, -Constant.UNIT_SIZE,
                Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                0, 0, -Constant.UNIT_SIZE,
                Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                0, 0, -Constant.UNIT_SIZE,
                -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                -Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                0, 0, -Constant.UNIT_SIZE,
                -Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                //左面
                -Constant.UNIT_SIZE, 0, 0,
                -Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                -Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                -Constant.UNIT_SIZE, 0, 0,
                -Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                -Constant.UNIT_SIZE, 0, 0,
                -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                -Constant.UNIT_SIZE, 0, 0,
                -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                -Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                //右面
                Constant.UNIT_SIZE, 0, 0,
                Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                Constant.UNIT_SIZE, 0, 0,
                Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                Constant.UNIT_SIZE, 0, 0,
                Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                Constant.UNIT_SIZE, 0, 0,
                Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                //上面
                0, Constant.UNIT_SIZE, 0,
                Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                0, Constant.UNIT_SIZE, 0,
                Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                -Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                0, Constant.UNIT_SIZE, 0,
                -Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                -Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                0, Constant.UNIT_SIZE, 0,
                -Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                //下面
                0, -Constant.UNIT_SIZE, 0,
                Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                0, -Constant.UNIT_SIZE, 0,
                -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,
                -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                0, -Constant.UNIT_SIZE, 0,
                -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                0, -Constant.UNIT_SIZE, 0,
                Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
                Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,
        };

        mVertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertices);
        mVertexBuffer.position(0);

        //顶点颜色值数组，每个顶点4个色彩值RGBA
        float colors[] = new float[]{
                //前面
                1, 1, 1, 0,//中间为白色
                1, 0, 0, 0,
                1, 0, 0, 0,
                1, 1, 1, 0,//中间为白色
                1, 0, 0, 0,
                1, 0, 0, 0,
                1, 1, 1, 0,//中间为白色
                1, 0, 0, 0,
                1, 0, 0, 0,
                1, 1, 1, 0,//中间为白色
                1, 0, 0, 0,
                1, 0, 0, 0,
                //后面
                1, 1, 1, 0,//中间为白色
                0, 0, 1, 0,
                0, 0, 1, 0,
                1, 1, 1, 0,//中间为白色
                0, 0, 1, 0,
                0, 0, 1, 0,
                1, 1, 1, 0,//中间为白色
                0, 0, 1, 0,
                0, 0, 1, 0,
                1, 1, 1, 0,//中间为白色
                0, 0, 1, 0,
                0, 0, 1, 0,
                //左面
                1, 1, 1, 0,//中间为白色
                1, 0, 1, 0,
                1, 0, 1, 0,
                1, 1, 1, 0,//中间为白色
                1, 0, 1, 0,
                1, 0, 1, 0,
                1, 1, 1, 0,//中间为白色
                1, 0, 1, 0,
                1, 0, 1, 0,
                1, 1, 1, 0,//中间为白色
                1, 0, 1, 0,
                1, 0, 1, 0,
                //右面
                1, 1, 1, 0,//中间为白色
                1, 1, 0, 0,
                1, 1, 0, 0,
                1, 1, 1, 0,//中间为白色
                1, 1, 0, 0,
                1, 1, 0, 0,
                1, 1, 1, 0,//中间为白色
                1, 1, 0, 0,
                1, 1, 0, 0,
                1, 1, 1, 0,//中间为白色
                1, 1, 0, 0,
                1, 1, 0, 0,
                //上面
                1, 1, 1, 0,//中间为白色
                0, 1, 0, 0,
                0, 1, 0, 0,
                1, 1, 1, 0,//中间为白色
                0, 1, 0, 0,
                0, 1, 0, 0,
                1, 1, 1, 0,//中间为白色
                0, 1, 0, 0,
                0, 1, 0, 0,
                1, 1, 1, 0,//中间为白色
                0, 1, 0, 0,
                0, 1, 0, 0,
                //下面
                1, 1, 1, 0,//中间为白色
                0, 1, 1, 0,
                0, 1, 1, 0,
                1, 1, 1, 0,//中间为白色
                0, 1, 1, 0,
                0, 1, 1, 0,
                1, 1, 1, 0,//中间为白色
                0, 1, 1, 0,
                0, 1, 1, 0,
                1, 1, 1, 0,//中间为白色
                0, 1, 1, 0,
                0, 1, 1, 0,
        };
        //创建顶点着色数据缓冲
        mColorBuffer = ByteBuffer.allocateDirect(colors.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(colors);
        mColorBuffer.position(0);//设置缓冲区起始位置
    }

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
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
    }
}
