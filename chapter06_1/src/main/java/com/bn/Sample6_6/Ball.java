package com.bn.Sample6_6;

import static com.bn.Sample6_6.Constant.UNIT_SIZE;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

//��
public class Ball {
    int mProgram;// �Զ�����Ⱦ������ɫ������id
    int muMVPMatrixHandle;// �ܱ任��������
    int maPositionHandle; // ����λ����������
    int muRadiusHandle;// ��İ뾶��������
    String mVertexShader;// ������ɫ��
    String mFragmentShader;// ƬԪ��ɫ��

    FloatBuffer mVertexBuffer;// �����������ݻ���
    private int vCount = 0; // ��������
    float yAngle = 0;// ��y����ת�ĽǶ�
    float xAngle = 0;// ��x����ת�ĽǶ�
    private float zAngle = 0;// ��z����ת�ĽǶ�
    private final float mRadius = 0.5f;

    public Ball(MySurfaceView mv) {
        initVertexData();
        initShader(mv);
    }

    // ��ʼ�������������ݵķ���
    public void initVertexData() {
        // �����������ݵĳ�ʼ��================begin============================
        ArrayList<Float> allVertices = new ArrayList<>();// ��Ŷ�������ֵ��ArrayList
        final int angleSpan = 5; //������е�λ�зֵĽǶ�
        for (int vAngle = -90; vAngle < 90; vAngle = vAngle + angleSpan) { //ά�ȷ���angleSpan��һ��
            for (int hAngle = 0; hAngle <= 360; hAngle = hAngle + angleSpan) { //���ȷ���angleSpan��һ��
                //������Ե�ǰ���ȡ�ά��λ�õĶ���Ϊ���ϲ����ı���4�����������
                float x0 = (float) (mRadius * UNIT_SIZE * Math.cos(Math.toRadians(vAngle)) * //��1�����������
                        Math.cos(Math.toRadians(hAngle)));
                float y0 = (float) (mRadius * UNIT_SIZE * Math.cos(Math.toRadians(vAngle)) *
                        Math.sin(Math.toRadians(hAngle)));
                float z0 = (float) (mRadius * UNIT_SIZE * Math.sin(Math.toRadians(vAngle)));

                float x1 = (float) (mRadius * UNIT_SIZE * Math.cos(Math.toRadians(vAngle)) * //��2�����������
                        Math.cos(Math.toRadians(hAngle + angleSpan)));
                float y1 = (float) (mRadius * UNIT_SIZE * Math.cos(Math.toRadians(vAngle)) *
                        Math.sin(Math.toRadians(hAngle + angleSpan)));
                float z1 = (float) (mRadius * UNIT_SIZE * Math.sin(Math.toRadians(vAngle)));

                float x2 = (float) (mRadius * UNIT_SIZE * Math.cos(Math.toRadians(vAngle + angleSpan)) * //��3�����������
                        Math.cos(Math.toRadians(hAngle + angleSpan)));
                float y2 = (float) (mRadius * UNIT_SIZE * Math.cos(Math.toRadians(vAngle + angleSpan)) *
                        Math.sin(Math.toRadians(hAngle + angleSpan)));
                float z2 = (float) (mRadius * UNIT_SIZE * Math.sin(Math.toRadians(vAngle + angleSpan)));

                float x3 = (float) (mRadius * UNIT_SIZE * Math.cos(Math.toRadians(vAngle + angleSpan)) * //��4�����������
                        Math.cos(Math.toRadians(hAngle)));
                float y3 = (float) (mRadius * UNIT_SIZE * Math.cos(Math.toRadians(vAngle + angleSpan)) * Math.sin(Math.toRadians(hAngle)));
                float z3 = (float) (mRadius * UNIT_SIZE * Math.sin(Math.toRadians(vAngle + angleSpan)));

                //��4����������갴�վ��Ƴ����������ε���Ҫһ�δ����б�
                allVertices.add(x1);
                allVertices.add(y1);
                allVertices.add(z1);

                allVertices.add(x3);
                allVertices.add(y3);
                allVertices.add(z3);

                allVertices.add(x0);
                allVertices.add(y0);
                allVertices.add(z0);

                allVertices.add(x1);
                allVertices.add(y1);
                allVertices.add(z1);

                allVertices.add(x2);
                allVertices.add(y2);
                allVertices.add(z2);

                allVertices.add(x3);
                allVertices.add(y3);
                allVertices.add(z3);
            }
        }
        vCount = allVertices.size() / 3;// ���������Ϊ����ֵ������1/3����Ϊһ��������3������

        // ��alVertix�е�����ֵת�浽һ��float������
        float[] vertices = new float[vCount * 3];
        for (int i = 0; i < allVertices.size(); i++) {
            vertices[i] = allVertices.get(i);
        }

        // ���������������ݻ���
        mVertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertices);
        mVertexBuffer.position(0);// ���û�������ʼλ��
    }

    // ��ʼ��shader
    public void initShader(MySurfaceView mv) {
        // ���ض�����ɫ���Ľű�����
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        // ����ƬԪ��ɫ���Ľű�����
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        // ���ڶ�����ɫ����ƬԪ��ɫ����������
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        // ��ȡ�����ж���λ����������
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        // ��ȡ�������ܱ任��������
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        // ��ȡ��������뾶����
        muRadiusHandle = GLES20.glGetUniformLocation(mProgram, "uRadius");
    }

    public void drawSelf() {
        MatrixState.rotate(xAngle, 1, 0, 0);//��X��ת��
        MatrixState.rotate(yAngle, 0, 1, 0);//��Y��ת��
        MatrixState.rotate(zAngle, 0, 0, 1);//��Z��ת��
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glUniform1f(muRadiusHandle, mRadius * UNIT_SIZE);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
    }
}
