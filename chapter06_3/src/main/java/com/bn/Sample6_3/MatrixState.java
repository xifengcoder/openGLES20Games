package com.bn.Sample6_3;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class MatrixState {
    private static float[] mMVPMatrix = new float[16];//获取具体物体的总变换矩阵
    private static float[] mProjectionMatrix = new float[16]; //4x4矩阵 投影用
    private static float[] mViewMatrix = new float[16]; //摄像机位置朝向9参数矩阵
    private static float[] mCurrModelMatrix; //当前变换矩阵

    public static float[] lightLocation = new float[]{0, 0, 0};//定位光光源位置
    private static final float[][] mStack = new float[10][16]; //保护变换矩阵的栈

    private static int stackTop = -1;
    private static FloatBuffer mLightPosByteBuffer;

    //获取变换初始矩阵
    public static void setInitStack() {
        mCurrModelMatrix = new float[16];
        Matrix.setRotateM(mCurrModelMatrix, 0, 0, 0, 0, 1);
    }

    //保护变换矩阵
    public static void pushMatrix() {
        stackTop++;
        System.arraycopy(mCurrModelMatrix, 0, mStack[stackTop], 0, 16);
    }

    //恢复变换矩阵
    public static void popMatrix() {
        System.arraycopy(mStack[stackTop], 0, mCurrModelMatrix, 0, 16);
        stackTop--;
    }

    //设置沿xyz轴移动
    public static void translate(float x, float y, float z) {
        Matrix.translateM(mCurrModelMatrix, 0, x, y, z);
    }

    //设置绕xyz轴移动
    public static void rotate(float angle, float x, float y, float z) {
        Matrix.rotateM(mCurrModelMatrix, 0, angle, x, y, z);
    }

    public static void setCamera(float eyeX, float eyeY, float eyeZ,
                                 float centerX, float centerY, float centerZ,
                                 float upX, float upY, float upZ) {
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    /**
     * 设置透视投影参数
     *
     * @param left   near面的left
     * @param right  near面的right
     * @param bottom near面的bottom
     * @param top    near面的top
     * @param near   near面距离
     * @param far    far面距离
     */
    public static void setProjectFrustum(float left, float right, float bottom, float top, float near, float far) {
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    public static float[] getFinalMatrix() {
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mCurrModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        return mMVPMatrix;
    }

    //获取具体物体的变换矩阵
    public static float[] getMMatrix() {
        return mCurrModelMatrix;
    }

    public static void setLightLocation(float x, float y, float z) {
        lightLocation[0] = x;
        lightLocation[1] = y;
        lightLocation[2] = z;
        mLightPosByteBuffer = ByteBuffer.allocateDirect(3 * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(lightLocation);
        mLightPosByteBuffer.position(0);
    }

    public static FloatBuffer getLightLocationBuffer() {
        return mLightPosByteBuffer;
    }
}
