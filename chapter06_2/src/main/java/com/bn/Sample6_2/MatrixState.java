package com.bn.Sample6_2;

import android.opengl.Matrix;

import java.nio.ByteBuffer;

//�洢ϵͳ����״̬����
public class MatrixState {
    private static float[] mMVPMatrix = new float[16]; //��ȡ����������ܱ任����
    private static float[] mProjMatrix = new float[16]; //4x4���� ͶӰ��
    private static float[] mVMatrix = new float[16]; //�����λ�ó���9��������
    private static float[] currMatrix; //��ǰ�任����

    //�����任�����ջ
    static float[][] mStack = new float[10][16];
    static int stackTop = -1;

    //��ȡ���任��ʼ����
    public static void setInitStack() {
        currMatrix = new float[16];
        Matrix.setRotateM(currMatrix, 0, 0, 1, 0, 0);
    }

    //�����任����
    public static void pushMatrix() {
        stackTop++;
        for (int i = 0; i < 16; i++) {
            mStack[stackTop][i] = currMatrix[i];
        }
    }

    //�ָ��任����
    public static void popMatrix() {
        for (int i = 0; i < 16; i++) {
            currMatrix[i] = mStack[stackTop][i];
        }
        stackTop--;
    }

    //������xyz���ƶ�
    public static void translate(float x, float y, float z) {
        Matrix.translateM(currMatrix, 0, x, y, z);
    }

    //������xyz���ƶ�
    public static void rotate(float angle, float x, float y, float z) {
        Matrix.rotateM(currMatrix, 0, angle, x, y, z);
    }

    public static void setCamera(float cx, float cy, float cz,
                                 float tx, float ty, float tz, float upx, float upy, float upz) {
        Matrix.setLookAtM(mVMatrix, 0, cx, cy, cz,
                tx, ty, tz, upx, upy, upz);
    }

    //����͸��ͶӰ����
    public static void setProjectFrustum(float left, float right,
                                         float bottom, float top, float near, float far) {
        Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    //��������ͶӰ����
    public static void setProjectOrtho(float left, float right,
                                       float bottom, float top, float near, float far) {
        Matrix.orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }


    public static float[] getFinalMatrix() {
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        return mMVPMatrix;
    }

    //��ȡ��������ı任����
    public static float[] getMMatrix() {
        return currMatrix;
    }
}
