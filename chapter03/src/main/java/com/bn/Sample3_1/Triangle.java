package com.bn.Sample3_1;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//������
public class Triangle {
    public static float[] mProjMatrix = new float[16];//4x4���� ͶӰ��
    public static float[] mVMatrix = new float[16];//�����λ�ó���9��������
    public static float[] mMVPMatrix;//��������õ��ܱ任����
    public static float[] mMMatrix = new float[16];//����������ƶ���ת������ת��ƽ��

    private int mProgram;//�Զ�����Ⱦ���߳���id
    private int mMVPMatrixHandle;//�ܱ任��������id
    private int mPositionHandle; //����λ����������id
    private int mColorHandle; //������ɫ��������id
    private FloatBuffer mVertexBuffer;//�����������ݻ���
    private FloatBuffer mColorBuffer;//������ɫ���ݻ���

    float xAngle = 0;//��x����ת�ĽǶ�

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
        //�����������ݵĳ�ʼ��
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

    //��ʼ��shader
    public void initShader(MyTDView mv) {
        //���ض�����ɫ���Ľű�����
        //������ɫ��
        String vertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        //����ƬԪ��ɫ���Ľű�����
        //ƬԪ��ɫ��
        String fragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        //���ڶ�����ɫ����ƬԪ��ɫ����������
        mProgram = ShaderUtil.createProgram(vertexShader, fragmentShader);
        //��ȡ�����ж���λ����������id  
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //��ȡ�����ж�����ɫ��������id  
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        //��ȡ�������ܱ任��������id
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void drawSelf() {
        //�ƶ�ʹ��ĳ��shader����
        GLES20.glUseProgram(mProgram);
        //��ʼ���任����
        Matrix.setRotateM(mMMatrix, 0, 0, 0, 1, 0);
        //������Z������λ��1
        //Matrix.translateM(mMMatrix, 0, 0, 0, 1);
        //������x����ת
        Matrix.rotateM(mMMatrix, 0, xAngle, 1, 0, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, Triangle.getFinalMatrix(mMMatrix), 0);
        //Ϊ����ָ������λ������
        GLES20.glVertexAttribPointer(mPositionHandle, 3,
                GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer
        );
        GLES20.glVertexAttribPointer(mColorHandle, 4,
                GLES20.GL_FLOAT, false, 4 * 4, mColorBuffer
        );
        //������λ����������
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mColorHandle);
        //����������
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }

    public static float[] getFinalMatrix(float[] spec) {
        mMVPMatrix = new float[16];
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, spec, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        return mMVPMatrix;
    }
}