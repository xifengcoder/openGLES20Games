package com.bn.Sample7_1;

import static com.bn.Sample7_1.ShaderUtil.createProgram;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//纹理三角形
public class Triangle {
    private static final int VERTEX_COUNT = 3;

    int mProgram;//自定义渲染管线程序id
    int muMVPMatrixHandle;//总变换矩阵引用id
    int maPositionHandle; //顶点位置属性引用id
    int maTexCoorHandle; //顶点纹理坐标属性引用id
    String mVertexShader;//顶点着色器
    String mFragmentShader;//片元着色器

    FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    FloatBuffer mTexCoorBuffer;//顶点纹理坐标数据缓冲
    float xAngle = 0;//绕x轴旋转的角度
    float yAngle = 0;//绕y轴旋转的角度
    float zAngle = 0;//绕z轴旋转的角度

    public Triangle(MySurfaceView mv) {
        //初始化顶点坐标与着色数据
        initVertexData();
        //初始化着色器
        initShader(mv);
    }

    //初始化顶点坐标与着色数据的方法
    public void initVertexData() {
        //顶点坐标数据的初始化================begin============================
        final float UNIT_SIZE = 0.15f;
        float vertices[] = new float[]{ //顶点坐标数据数组
                0 * UNIT_SIZE, 11 * UNIT_SIZE, 0, //上面顶点的坐标
                -11 * UNIT_SIZE, -11 * UNIT_SIZE, 0, //左下侧顶点的坐标
                11 * UNIT_SIZE, -11 * UNIT_SIZE, 0, //右下侧顶点的坐标
        };

        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        mVertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertices);
        mVertexBuffer.position(0);//设置缓冲区起始位置

        //顶点颜色值数组，每个顶点4个色彩值RGBA
        float texCoor[] = new float[]{
                0.5f, 0,
                0, 1,
                1, 1
        };

        //创建顶点纹理坐标数据缓冲
        mTexCoorBuffer = ByteBuffer.allocateDirect(texCoor.length * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(texCoor);
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
    }

    //初始化着色器
    public void initShader(MySurfaceView mv) {
        //加载顶点着色器的脚本内容
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgram = createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用id
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用id
        maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void drawSelf(int texId) {
        //制定使用某套shader程序
        GLES20.glUseProgram(mProgram);
        MatrixState.setInitStack();
        //设置沿Z轴正向位移1
        MatrixState.transtate(0, 0, 1);
        //设置绕y轴旋转
        MatrixState.rotate(yAngle, 0, 1, 0);
        //设置绕z轴旋转
        MatrixState.rotate(zAngle, 0, 0, 1);
        //设置绕x轴旋转
        MatrixState.rotate(xAngle, 1, 0, 0);
        //将最终变换矩阵传入shader程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        //为画笔指定顶点位置数据
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, mVertexBuffer
        );
        //为画笔指定顶点纹理坐标数据
        GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT,
                false, 2 * 4, mTexCoorBuffer);
        //允许顶点位置数据数组
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);

        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);

        //绘制纹理矩形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, VERTEX_COUNT);
    }
}
