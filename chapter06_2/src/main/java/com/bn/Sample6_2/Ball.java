package com.bn.Sample6_2;

import android.opengl.GLES20;

import com.yxf.opengl.common.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

//球
public class Ball {
    public static final float UNIT_SIZE = 1f;
    private static final int ANGLE_SPAN = 10;// 将球进行单位切分的角度
    private static final float RADIUS = 0.8f;

    int mProgram;// 自定义渲染管线着色器程序id
    int muMVPMatrixHandle;// 总变换矩阵引用
    int maPositionHandle; // 顶点位置属性引用
    int muRHandle;// 球的半径属性引用
    String mVertexShader;// 顶点着色器
    String mFragmentShader;// 片元着色器

    FloatBuffer mVertexBuffer;// 顶点坐标数据缓冲
    int vCount = 0;
    float yAngle = 0;// 绕y轴旋转的角度
    float xAngle = 0;// 绕x轴旋转的角度
    float zAngle = 0;// 绕z轴旋转的角度

    public Ball(MySurfaceView mv) {
        initVertexData();
        initShader(mv);
    }

    // 初始化顶点坐标数据的方法
    public void initVertexData() {
        // 顶点坐标数据的初始化================begin============================
        ArrayList<Float> allVertices = new ArrayList<Float>();// 存放顶点坐标的ArrayList
        for (int vAngle = -90; vAngle < 90; vAngle = vAngle + ANGLE_SPAN) {
            // 垂直方向angleSpan度一份
            for (int hAngle = 0; hAngle <= 360; hAngle = hAngle + ANGLE_SPAN) {
                // 纵向横向各到一个角度后计算对应的此点在球面上的坐标
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

                // 将计算出来的XYZ坐标加入存放顶点坐标的ArrayList
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
        MatrixState.rotate(xAngle, 1, 0, 0);//绕X轴转动
        MatrixState.rotate(yAngle, 0, 1, 0);//绕Y轴转动
        MatrixState.rotate(zAngle, 0, 0, 1);//绕Z轴转动
        GLES20.glUseProgram(mProgram);
        // 将最终变换矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        // 将半径尺寸传入着色器程序
        GLES20.glUniform1f(muRHandle, RADIUS * UNIT_SIZE);
        // 将顶点位置数据传入渲染管线
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
    }
}
