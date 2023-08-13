package com.bn.Sample3_1;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TriangleRender implements GLSurfaceView.Renderer {

    private static final float ANGLE_SPAN = 1.0f;
    private static final String TAG = "yxf";

    public static float[] mProjectionMatrix = new float[16]; //4x4矩阵投影矩阵
    public static float[] mViewMatrix = new float[16]; //摄像机位置朝向的参数矩阵
    public static float[] mModelMatrix = new float[16]; //具体物体的3D变换矩阵，包括旋转、平移、缩放
    public static float[] mMVPMatrix = new float[16]; //总变换矩阵

    private int mProgram; //自定义渲染管线着色器程序id
    private int mMVPMatrixHandle; //总变换矩阵引用id
    private int mPositionHandle; //顶点位置属性引用id
    private int mColorHandle; //顶点颜色属性引用id

    private FloatBuffer mVertexBuffer; //顶点坐标数据缓冲
    private FloatBuffer mColorBuffer; //顶点着色数据缓冲

    private final MyTDView mMyTDView;

    private float xAngle = 0; //绕x轴旋转的角度

    public static final float[] COLORS = new float[]{
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 1f, 1f, 0f,
    };

    public static final float[] VERTICES = new float[]{
            -0.8f, 0, 0,
            0, 0.8f, 0,
            0.8f, 0, 0
    };

    public TriangleRender(MyTDView myTDView) {
        this.mMyTDView = myTDView;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.5f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        initVertexData();
        initShader();
        RotateThread rotateThread = new RotateThread();
        rotateThread.start();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        draw();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        //计算GLSurfaceView的宽高比
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1f, 1000);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 2.5f,
                0f, 0f, 0f,
                0f, 1f, 0.0f);
    }

    private void initVertexData() {
        //顶点坐标数据的初始化
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

    private void initShader() {
        String vertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mMyTDView.getResources());
        String fragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mMyTDView.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(vertexShader, fragmentShader);
        //获取程序中顶点位置属性引用id
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点颜色属性引用id
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        //获取程序中总变换矩阵引用id
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    private void draw() {
        GLES20.glUseProgram(mProgram); //制定使用某套shader程序
        Matrix.setRotateM(mModelMatrix, 0, 0, 1, 0, 0); //初始化变换矩阵
        Matrix.translateM(mModelMatrix, 0, 0, 0, 0);  //平移
        //Matrix.rotateM(mModelMatrix, 0, xAngle, 1, 0, 0); //旋转
        //mMVPMatrix = mProjMatrix x mViewMatrix x mModelMatrix;
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        //将总变换矩阵变量传入渲染管线
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        //将顶点位置数据传送进渲染管线
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        //将顶点颜色数据传送进渲染管线
        GLES20.glVertexAttribPointer(mColorHandle, 3, GLES20.GL_FLOAT, false, 4 * 4, mColorBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle); //启用顶点位置数据
        GLES20.glEnableVertexAttribArray(mColorHandle); //启用顶点着色数据
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3); //绘制三角形
    }

    private class RotateThread extends Thread {
        public boolean flag = true;

        @Override
        public void run() {
            while (flag) {
                xAngle += ANGLE_SPAN;
                try {
                    Thread.sleep(20);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

