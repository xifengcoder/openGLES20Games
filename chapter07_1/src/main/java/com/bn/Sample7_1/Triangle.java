package com.bn.Sample7_1;

import android.opengl.GLES20;

import com.yxf.opengl.common.MatrixState;
import com.yxf.opengl.common.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {
    private static final int VERTEX_COUNT = 3;
    private static final float UNIT_SIZE = 0.15f;

    private int mProgram;//自定义渲染管线程序id
    private int uMVPMatrixHandle;//总变换矩阵引用id
    private int aPositionHandle; //顶点位置属性引用id
    private int aTextureCoordHandle; //顶点纹理坐标属性引用id

    private FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    private FloatBuffer mTextureCoordBuffer;//顶点纹理坐标数据缓冲
    float xAngle = 0;//绕x轴旋转的角度
    float yAngle = 0;//绕y轴旋转的角度
    float zAngle = 0;//绕z轴旋转的角度

    public Triangle(MySurfaceView mv) {
        initVertexData();
        initShader(mv);
    }

    //初始化顶点坐标与着色数据的方法
    private void initVertexData() {
        float[] vertices = new float[]{ //顶点坐标数据数组
                0 * UNIT_SIZE, 11 * UNIT_SIZE, 0, //上面顶点的坐标
                -11 * UNIT_SIZE, -11 * UNIT_SIZE, 0, //左下侧顶点的坐标
                11 * UNIT_SIZE, -11 * UNIT_SIZE, 0, //右下侧顶点的坐标
        };

        mVertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertices);
        mVertexBuffer.position(0);

        //顶点纹理坐标数组的初始化
        float[] textureCoord = new float[]{
                0.5f, 0,
                0, 1,
                1, 1
        };

        mTextureCoordBuffer = ByteBuffer.allocateDirect(textureCoord.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureCoord);
        mTextureCoordBuffer.position(0);//设置缓冲区起始位置
    }

    private void initShader(MySurfaceView mv) {
        String vertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        String fragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        mProgram = ShaderUtil.createProgram(vertexShader, fragmentShader);
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        aPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        aTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
    }

    public void drawSelf(int texId) {
        GLES20.glUseProgram(mProgram);
        MatrixState.setInitStack();
        MatrixState.translate(0, 0, 1); //设置沿Z轴正向位移1
        MatrixState.rotate(yAngle, 0, 1, 0); //设置绕y轴旋转
        MatrixState.rotate(zAngle, 0, 0, 1); //设置绕z轴旋转
        MatrixState.rotate(xAngle, 1, 0, 0); //设置绕x轴旋转
        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        GLES20.glVertexAttribPointer(aTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, mTextureCoordBuffer);
        GLES20.glEnableVertexAttribArray(aPositionHandle);
        GLES20.glEnableVertexAttribArray(aTextureCoordHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, VERTEX_COUNT);
    }
}
