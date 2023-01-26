package com.bn.Sample6_3;

import static com.bn.Sample6_3.Constant.UNIT_SIZE;

import android.opengl.GLES20;

import com.yxf.opengl.common.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

//球
public class Ball {
    private static final float RADIUS = 1.0f;

    private int mProgram;// 自定义渲染管线着色器程序id
    private int muMVPMatrixHandle;// 总变换矩阵引用
    private int muMMatrixHandle;//位置、旋转变换矩阵引用
    private int maPositionHandle; // 顶点位置属性引用
    private int muRHandle;// 球的半径属性引用
    private int maNormalHandle; //顶点法向量属性引用
    private int maLightLocationHandle;//光源位置属性引用

    private String mVertexShader;// 顶点着色器
    private String mFragmentShader;// 片元着色器

    private FloatBuffer mVertexBuffer;// 顶点坐标数据缓冲
    private int vCount = 0;

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
        final int angleSpan = 10;// 将球进行单位切分的角度
        for (int vAngle = -90; vAngle < 90; vAngle = vAngle + angleSpan)// 垂直方向angleSpan度一份
        {
            for (int hAngle = 0; hAngle <= 360; hAngle = hAngle + angleSpan)// 水平方向angleSpan度一份
            {// 纵向横向各到一个角度后计算对应的此点在球面上的坐标
                float x0 = (float) (RADIUS * UNIT_SIZE
                        * Math.cos(Math.toRadians(vAngle)) * Math.cos(Math
                        .toRadians(hAngle)));
                float y0 = (float) (RADIUS * UNIT_SIZE
                        * Math.cos(Math.toRadians(vAngle)) * Math.sin(Math
                        .toRadians(hAngle)));
                float z0 = (float) (RADIUS * UNIT_SIZE * Math.sin(Math
                        .toRadians(vAngle)));

                float x1 = (float) (RADIUS * UNIT_SIZE
                        * Math.cos(Math.toRadians(vAngle)) * Math.cos(Math
                        .toRadians(hAngle + angleSpan)));
                float y1 = (float) (RADIUS * UNIT_SIZE
                        * Math.cos(Math.toRadians(vAngle)) * Math.sin(Math
                        .toRadians(hAngle + angleSpan)));
                float z1 = (float) (RADIUS * UNIT_SIZE * Math.sin(Math
                        .toRadians(vAngle)));

                float x2 = (float) (RADIUS * UNIT_SIZE
                        * Math.cos(Math.toRadians(vAngle + angleSpan)) * Math
                        .cos(Math.toRadians(hAngle + angleSpan)));
                float y2 = (float) (RADIUS * UNIT_SIZE
                        * Math.cos(Math.toRadians(vAngle + angleSpan)) * Math
                        .sin(Math.toRadians(hAngle + angleSpan)));
                float z2 = (float) (RADIUS * UNIT_SIZE * Math.sin(Math
                        .toRadians(vAngle + angleSpan)));

                float x3 = (float) (RADIUS * UNIT_SIZE
                        * Math.cos(Math.toRadians(vAngle + angleSpan)) * Math
                        .cos(Math.toRadians(hAngle)));
                float y3 = (float) (RADIUS * UNIT_SIZE
                        * Math.cos(Math.toRadians(vAngle + angleSpan)) * Math
                        .sin(Math.toRadians(hAngle)));
                float z3 = (float) (RADIUS * UNIT_SIZE * Math.sin(Math
                        .toRadians(vAngle + angleSpan)));

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

        vCount = allVertices.size() / 3; // 顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标
        float[] vertices = new float[vCount * 3];
        for (int i = 0; i < allVertices.size(); i++) {
            vertices[i] = allVertices.get(i);
        }

        mVertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mVertexBuffer.put(vertices).position(0);
    }

    public void initShader(MySurfaceView mv) {
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");
        muRHandle = GLES20.glGetUniformLocation(mProgram, "uR");
        maNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
        maLightLocationHandle = GLES20.glGetUniformLocation(mProgram, "uLightLocation");
    }

    public void drawSelf() {
        MatrixState.rotate(xAngle, 1, 0, 0);
        MatrixState.rotate(yAngle, 0, 1, 0);
        MatrixState.rotate(zAngle, 0, 0, 1);
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        GLES20.glUniform1f(muRHandle, RADIUS * UNIT_SIZE);
        GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixState.getLightLocationBuffer());
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        GLES20.glVertexAttribPointer(maNormalHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maNormalHandle);// 启用顶点法向量数据
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
    }
}
