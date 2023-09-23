package com.bn.Sample6.lighting;

import static com.yxf.opengl.common.Constant.UNIT_SIZE;

import android.opengl.GLES20;

import com.yxf.opengl.common.MatrixState;
import com.yxf.opengl.common.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

//球
public class Ball {
    int mProgram;// 自定义渲染管线着色器程序id
    int muMVPMatrixHandle;// 总变换矩阵引用
    int maPositionHandle; // 顶点位置属性引用
    int muRadiusHandle;// 球的半径属性引用
    String mVertexShader;// 顶点着色器
    String mFragmentShader;// 片元着色器

    FloatBuffer mVertexBuffer;// 顶点坐标数据缓冲
    private int vCount = 0; // 顶点数量
    float yAngle = 0;// 绕y轴旋转的角度
    float xAngle = 0;// 绕x轴旋转的角度
    private float zAngle = 0;// 绕z轴旋转的角度
    private final float mRadius = 0.5f;

    public Ball(MySurfaceView mv) {
        initVertexData();
        initShader(mv);
    }

    // 初始化顶点坐标数据的方法
    public void initVertexData() {
        // 顶点坐标数据的初始化================begin============================
        ArrayList<Float> allVertices = new ArrayList<>();// 存放顶点坐标值的ArrayList
        final int angleSpan = 5; //将球进行单位切分的角度
        for (int vAngle = -90; vAngle < 90; vAngle = vAngle + angleSpan) { //维度方向angleSpan度一份
            for (int hAngle = 0; hAngle <= 360; hAngle = hAngle + angleSpan) { //经度方向angleSpan度一份
                //计算出以当前经度、维度位置的顶点为左上侧点的四边形4个顶点的坐标
                float x0 = (float) (mRadius * UNIT_SIZE * Math.cos(Math.toRadians(vAngle)) * //第1个顶点的坐标
                        Math.cos(Math.toRadians(hAngle)));
                float y0 = (float) (mRadius * UNIT_SIZE * Math.cos(Math.toRadians(vAngle)) *
                        Math.sin(Math.toRadians(hAngle)));
                float z0 = (float) (mRadius * UNIT_SIZE * Math.sin(Math.toRadians(vAngle)));

                float x1 = (float) (mRadius * UNIT_SIZE * Math.cos(Math.toRadians(vAngle)) * //第2个顶点的坐标
                        Math.cos(Math.toRadians(hAngle + angleSpan)));
                float y1 = (float) (mRadius * UNIT_SIZE * Math.cos(Math.toRadians(vAngle)) *
                        Math.sin(Math.toRadians(hAngle + angleSpan)));
                float z1 = (float) (mRadius * UNIT_SIZE * Math.sin(Math.toRadians(vAngle)));

                float x2 = (float) (mRadius * UNIT_SIZE * Math.cos(Math.toRadians(vAngle + angleSpan)) * //第3个顶点的坐标
                        Math.cos(Math.toRadians(hAngle + angleSpan)));
                float y2 = (float) (mRadius * UNIT_SIZE * Math.cos(Math.toRadians(vAngle + angleSpan)) *
                        Math.sin(Math.toRadians(hAngle + angleSpan)));
                float z2 = (float) (mRadius * UNIT_SIZE * Math.sin(Math.toRadians(vAngle + angleSpan)));

                float x3 = (float) (mRadius * UNIT_SIZE * Math.cos(Math.toRadians(vAngle + angleSpan)) * //第4个顶点的坐标
                        Math.cos(Math.toRadians(hAngle)));
                float y3 = (float) (mRadius * UNIT_SIZE * Math.cos(Math.toRadians(vAngle + angleSpan)) * Math.sin(Math.toRadians(hAngle)));
                float z3 = (float) (mRadius * UNIT_SIZE * Math.sin(Math.toRadians(vAngle + angleSpan)));

                //将4个顶点的坐标按照卷绕成两个三角形的需要一次存入列表。
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
        vCount = allVertices.size() / 3;// 顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标

        // 将alVertix中的坐标值转存到一个float数组中
        float[] vertices = new float[vCount * 3];
        for (int i = 0; i < allVertices.size(); i++) {
            vertices[i] = allVertices.get(i);
        }

        // 创建顶点坐标数据缓冲
        mVertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertices);
        mVertexBuffer.position(0);// 设置缓冲区起始位置
    }

    // 初始化shader
    public void initShader(MySurfaceView mv) {
        // 加载顶点着色器的脚本内容
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        // 加载片元着色器的脚本内容
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        // 基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        // 获取程序中顶点位置属性引用
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        // 获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        // 获取程序中球半径引用
        muRadiusHandle = GLES20.glGetUniformLocation(mProgram, "uRadius");
    }

    public void drawSelf() {
        MatrixState.rotate(xAngle, 1, 0, 0);//绕X轴转动
        MatrixState.rotate(yAngle, 0, 1, 0);//绕Y轴转动
        MatrixState.rotate(zAngle, 0, 0, 1);//绕Z轴转动
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glUniform1f(muRadiusHandle, mRadius * UNIT_SIZE);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
    }
}
