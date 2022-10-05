package com.bn.Sample5_6;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//颜色点或线
public class PointsOrLines {
    int mProgram;// 自定义渲染管线着色器程序id
    int muMVPMatrixHandle;// 总变换矩阵引用
    int maPositionHandle; // 顶点位置属性引用
    int maColorHandle; // 顶点颜色属性引用
    String mVertexShader;// 顶点着色器代码脚本
    String mFragmentShader;// 片元着色器代码脚本

    FloatBuffer mVertexBuffer;// 顶点坐标数据缓冲
    FloatBuffer mColorBuffer;// 顶点着色数据缓冲

    private static final int VERTEX_COUNT = 5;

    private static float VERTICES[] = new float[]{
            0, 0, 0,
            Constant.UNIT_SIZE, Constant.UNIT_SIZE, 0,
            -Constant.UNIT_SIZE, Constant.UNIT_SIZE, 0,
            -Constant.UNIT_SIZE, -Constant.UNIT_SIZE, 0,
            Constant.UNIT_SIZE, -Constant.UNIT_SIZE, 0
    };

    public static final float[] COLORS = new float[]{
            1, 0, 0, 0,// 黄
            1, 1, 0, 0,// 白
            0, 1, 0, 0,// 绿
            1, 1, 1, 0,// 白
            0, 1, 1, 0,// 黄
    };

    public PointsOrLines(MySurfaceView mv) {
        initVertexData();
        initShader(mv);
    }

    public void initVertexData() {
        mVertexBuffer = ByteBuffer.allocateDirect(VERTICES.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(VERTICES);
        mVertexBuffer.position(0);

        mColorBuffer = ByteBuffer.allocateDirect(COLORS.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(COLORS);
        mColorBuffer.position(0);
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
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
                MatrixState.getFinalMatrix(), 0);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, mVertexBuffer);
        GLES20.glVertexAttribPointer(maColorHandle, 4, GLES20.GL_FLOAT, false,
                4 * 4, mColorBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maColorHandle);

        GLES20.glLineWidth(50);
        switch (Constant.CURR_DRAW_MODE) {
            case Constant.GL_POINTS:// GL_POINTS方式
                GLES20.glDrawArrays(GLES20.GL_POINTS, 0, VERTEX_COUNT);
                break;
            case Constant.GL_LINES:// GL_LINES方式
                GLES20.glDrawArrays(GLES20.GL_LINES, 0, VERTEX_COUNT);
                break;
            case Constant.GL_LINE_STRIP:// GL_LINE_STRIP方式
                GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, VERTEX_COUNT);
                break;
            case Constant.GL_LINE_LOOP:// GL_LINE_LOOP方式
                GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, VERTEX_COUNT);
                break;
        }
    }
}
