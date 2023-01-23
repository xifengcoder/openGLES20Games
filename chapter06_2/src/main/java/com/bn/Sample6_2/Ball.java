package com.bn.Sample6_2;

import android.opengl.GLES20;

import com.yxf.opengl.common.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

//��
public class Ball {
    public static final float UNIT_SIZE = 1f;
    private static final int ANGLE_SPAN = 10;// ������е�λ�зֵĽǶ�
    private static final float RADIUS = 0.8f;

    int mProgram;// �Զ�����Ⱦ������ɫ������id
    int muMVPMatrixHandle;// �ܱ任��������
    int maPositionHandle; // ����λ����������
    int muRHandle;// ��İ뾶��������
    String mVertexShader;// ������ɫ��
    String mFragmentShader;// ƬԪ��ɫ��

    FloatBuffer mVertexBuffer;// �����������ݻ���
    int vCount = 0;
    float yAngle = 0;// ��y����ת�ĽǶ�
    float xAngle = 0;// ��x����ת�ĽǶ�
    float zAngle = 0;// ��z����ת�ĽǶ�

    public Ball(MySurfaceView mv) {
        initVertexData();
        initShader(mv);
    }

    // ��ʼ�������������ݵķ���
    public void initVertexData() {
        // �����������ݵĳ�ʼ��================begin============================
        ArrayList<Float> allVertices = new ArrayList<Float>();// ��Ŷ��������ArrayList
        for (int vAngle = -90; vAngle < 90; vAngle = vAngle + ANGLE_SPAN) {
            // ��ֱ����angleSpan��һ��
            for (int hAngle = 0; hAngle <= 360; hAngle = hAngle + ANGLE_SPAN) {
                // ����������һ���ǶȺ�����Ӧ�Ĵ˵��������ϵ�����
                float x0 = (float) (RADIUS * UNIT_SIZE * Math.cos(Math.toRadians(vAngle)) * Math.cos(Math.toRadians(hAngle)));
                float y0 = (float) (RADIUS * UNIT_SIZE * Math.cos(Math.toRadians(vAngle)) * Math.sin(Math.toRadians(hAngle)));
                float z0 = (float) (RADIUS * UNIT_SIZE * Math.sin(Math.toRadians(vAngle)));
                float x1 = (float) (RADIUS * UNIT_SIZE * Math.cos(Math.toRadians(vAngle)) * Math.cos(Math.toRadians(hAngle + ANGLE_SPAN)));
                float y1 = (float) (RADIUS * UNIT_SIZE * Math.cos(Math.toRadians(vAngle)) * Math.sin(Math.toRadians(hAngle + ANGLE_SPAN)));
                float z1 = (float) (RADIUS * UNIT_SIZE * Math.sin(Math.toRadians(vAngle)));

                float x2 = (float) (RADIUS * UNIT_SIZE * Math.cos(Math.toRadians(vAngle + ANGLE_SPAN)) * Math.cos(Math.toRadians(hAngle + ANGLE_SPAN)));
                float y2 = (float) (RADIUS * UNIT_SIZE * Math.cos(Math.toRadians(vAngle + ANGLE_SPAN)) * Math.sin(Math.toRadians(hAngle + ANGLE_SPAN)));
                float z2 = (float) (RADIUS * UNIT_SIZE * Math.sin(Math.toRadians(vAngle + ANGLE_SPAN)));

                float x3 = (float) (RADIUS * UNIT_SIZE * Math.cos(Math.toRadians(vAngle + ANGLE_SPAN)) * Math.cos(Math.toRadians(hAngle)));
                float y3 = (float) (RADIUS * UNIT_SIZE * Math.cos(Math.toRadians(vAngle + ANGLE_SPAN)) * Math.sin(Math.toRadians(hAngle)));
                float z3 = (float) (RADIUS * UNIT_SIZE * Math.sin(Math.toRadians(vAngle + ANGLE_SPAN)));

                // �����������XYZ��������Ŷ��������ArrayList
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
        vCount = allVertices.size() / 3;

        float[] vertices = new float[vCount * 3];
        for (int i = 0; i < allVertices.size(); i++) {
            vertices[i] = allVertices.get(i);
        }

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
    }

    public void initShader(MySurfaceView mv) {
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        muRHandle = GLES20.glGetUniformLocation(mProgram, "uR");
    }

    public void drawSelf() {
        MatrixState.rotate(xAngle, 1, 0, 0);//��X��ת��
        MatrixState.rotate(yAngle, 0, 1, 0);//��Y��ת��
        MatrixState.rotate(zAngle, 0, 0, 1);//��Z��ת��
        GLES20.glUseProgram(mProgram);
        // �����ձ任��������ɫ������
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        // ���뾶�ߴ紫����ɫ������
        GLES20.glUniform1f(muRHandle, RADIUS * UNIT_SIZE);
        // ������λ�����ݴ�����Ⱦ����
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
    }
}
