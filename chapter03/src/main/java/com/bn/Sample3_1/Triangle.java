package com.bn.Sample3_1;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//三角形
public class Triangle {
    public static float[] mProjMatrix = new float[16];//4x4矩阵 投影用
    public static float[] mVMatrix = new float[16];//摄像机位置朝向9参数矩阵
    public static float[] mMVPMatrix;//最后起作用的总变换矩阵
    public static float[] mMMatrix = new float[16];//具体物体的移动旋转矩阵，旋转、平移

    private int mProgram;//自定义渲染管线程序id
    private int mMVPMatrixHandle;//总变换矩阵引用id
    private int mPositionHandle; //顶点位置属性引用id
    private int mColorHandle; //顶点颜色属性引用id
    private FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    private FloatBuffer mColorBuffer;//顶点着色数据缓冲

    float xAngle = 0;//绕x轴旋转的角度

    public static final float[] COLORS = new float[]{
            1, 1, 1, 0,
            0, 0, 1, 0,
            0, 1, 0, 0
    };
    public static final float[] VERTICES = new float[]{
            -0.8f, 0, 0,
            0, -0.8f, 0,
            0.8f, 0, 0
    };

    public Triangle(MyTDView mv) {
        initVertexData();
        initShader(mv);
    }

    public void initVertexData() {
        //顶点坐标数据的初始化
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

    //初始化shader
    public void initShader(MyTDView mv) {
        //加载顶点着色器的脚本内容
        //顶点着色器
        String vertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        //加载片元着色器的脚本内容
        //片元着色器
        String fragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(vertexShader, fragmentShader);
        //获取程序中顶点位置属性引用id  
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点颜色属性引用id  
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        //获取程序中总变换矩阵引用id
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void drawSelf() {
        //制定使用某套shader程序
        GLES20.glUseProgram(mProgram);
        //初始化变换矩阵
        Matrix.setRotateM(mMMatrix, 0, 0, 0, 1, 0);
        //设置沿Z轴正向位移1
        //Matrix.translateM(mMMatrix, 0, 0, 0, 1);
        //设置绕x轴旋转
        Matrix.rotateM(mMMatrix, 0, xAngle, 1, 0, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, Triangle.getFinalMatrix(mMMatrix), 0);
        //为画笔指定顶点位置数据
        GLES20.glVertexAttribPointer(mPositionHandle, 3,
                GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer
        );
        GLES20.glVertexAttribPointer(mColorHandle, 4,
                GLES20.GL_FLOAT, false, 4 * 4, mColorBuffer
        );
        //允许顶点位置数据数组
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mColorHandle);
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }

    public static float[] getFinalMatrix(float[] spec) {
        mMVPMatrix = new float[16];
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, spec, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        return mMVPMatrix;
    }
}