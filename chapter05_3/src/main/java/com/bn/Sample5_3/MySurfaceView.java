package com.bn.Sample5_3;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class MySurfaceView extends GLSurfaceView {
    private SceneRenderer mRenderer;

    public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2);
        mRenderer = new SceneRenderer();
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染
    }

    private class SceneRenderer implements GLSurfaceView.Renderer {
        Cube cube;//立方体

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            cube = new Cube(MySurfaceView.this);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glEnable(GLES20.GL_CULL_FACE);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            //绘制原立方体
            MatrixState.pushMatrix();
            cube.drawSelf();
            MatrixState.popMatrix();

            //绘制变换后的立方体
            MatrixState.pushMatrix();
            MatrixState.translate(5, 0, 0);//沿x方向平移3
            cube.drawSelf();
            MatrixState.popMatrix();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            Constant.ratio = (float) width / height;
            MatrixState.setProjectFrustum(-Constant.ratio * 0.8f, Constant.ratio * 1.2f, -1, 1, 20, 100);
            MatrixState.setCamera(0f, 0f, 45, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            //初始化变换矩阵
            MatrixState.setInitStack();
        }
    }
}
