package com.bn.Sample3_1;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyTDView extends GLSurfaceView {
    private static final float ANGLE_SPAN = 1.0f;

    RotateThread mRotateThread;
    SceneRenderer mRenderer;

    public MyTDView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2);
        mRenderer = new SceneRenderer();
        this.setRenderer(mRenderer);
        this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    private class SceneRenderer implements Renderer {
        Triangle triangle;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.5f);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //创建三角形对对象
            triangle = new Triangle(MyTDView.this);
            mRotateThread = new RotateThread();
            mRotateThread.start();
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            triangle.drawSelf();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            //计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            Matrix.frustumM(Triangle.mProjMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
            //调用此方法产生摄像机9参数位置矩阵
            Matrix.setLookAtM(Triangle.mVMatrix, 0, 0, 0, 3,
                    0f, 0f, 0f,
                    0f, 1.0f, 0.0f);
        }
    }

    public class RotateThread extends Thread {
        public boolean flag = true;

        @Override
        public void run() {
            while (flag) {
                mRenderer.triangle.xAngle = mRenderer.triangle.xAngle + ANGLE_SPAN;
                try {
                    Thread.sleep(20);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}