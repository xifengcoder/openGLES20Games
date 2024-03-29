package com.bn.Sample7_2;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.yxf.opengl.common.MatrixState;
import com.yxf.opengl.common.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//纹理矩形
public class TextureRect {
    private static final float UNIT_SIZE = 0.3f;

    int mProgram;//自定义渲染管线程序id
    int muMVPMatrixHandle;//总变换矩阵引用id
    int maPositionHandle; //顶点位置属性引用id
    int maTexCoorHandle; //顶点纹理坐标属性引用id
    String mVertexShader;//顶点着色器
    String mFragmentShader;//片元着色器
    static float[] mMMatrix = new float[16];//具体物体的移动旋转矩阵

    FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    FloatBuffer mTexCoorBuffer;//顶点纹理坐标数据缓冲

    int vCount = 0;
    float xAngle = 0;//绕x轴旋转的角度
    float yAngle = 0;//绕y轴旋转的角度
    float zAngle = 0;//绕z轴旋转的角度

    float sRange;//s纹理坐标范围
    float tRange;//t纹理坐标范围

    public TextureRect(MySurfaceView mv, float sRange, float tRange) {
        //设置s纹理坐标范围
        this.sRange = sRange;
        //设置t纹理坐标范围
        this.tRange = tRange;

        initVertexData();
        initShader(mv);
    }

    //初始化顶点坐标与着色数据的方法
    public void initVertexData() {
        //顶点坐标数据的初始化================begin============================
        vCount = 6;
        float[] vertices = new float[]{
                -4 * UNIT_SIZE, 4 * UNIT_SIZE, 0,
                -4 * UNIT_SIZE, -4 * UNIT_SIZE, 0,
                4 * UNIT_SIZE, -4 * UNIT_SIZE, 0,

                4 * UNIT_SIZE, -4 * UNIT_SIZE, 0,
                4 * UNIT_SIZE, 4 * UNIT_SIZE, 0,
                -4 * UNIT_SIZE, 4 * UNIT_SIZE, 0
        };

        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        mVertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertices);
        mVertexBuffer.position(0);

        //顶点纹理坐标数据的初始化================begin============================
        //顶点颜色值数组，每个顶点4个色彩值RGBA
        float[] texCoor = new float[]{
                0, 0, 0, tRange, sRange, tRange,
                sRange, tRange, sRange, 0, 0, 0};

        //创建顶点纹理坐标数据缓冲
        mTexCoorBuffer = ByteBuffer.allocateDirect(texCoor.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(texCoor);
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
    }

    //初始化shader
    public void initShader(MySurfaceView mv) {
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void drawSelf(int texId) {
        GLES20.glUseProgram(mProgram);
        Matrix.setRotateM(mMMatrix, 0, 0, 0, 1, 0);
        Matrix.translateM(mMMatrix, 0, 0, 0, 1);
        Matrix.rotateM(mMMatrix, 0, yAngle, 0, 1, 0);
        Matrix.rotateM(mMMatrix, 0, zAngle, 0, 0, 1);
        Matrix.rotateM(mMMatrix, 0, xAngle, 1, 0, 0);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(mMMatrix), 0);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        //为画笔指定顶点纹理坐标数据
        GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, mTexCoorBuffer);
        //允许顶点位置数据数组
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);

        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);

        //绘制纹理矩形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
    }
}
