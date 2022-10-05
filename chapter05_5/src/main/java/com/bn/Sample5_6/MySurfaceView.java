package com.bn.Sample5_6;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class MySurfaceView extends GLSurfaceView {
    private SceneRenderer mRenderer;//场景渲染器

    public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        mRenderer = new SceneRenderer();    //创建场景渲染器
        setRenderer(mRenderer);                //设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染
        Constant.CURR_DRAW_MODE = Constant.GL_POINTS;//初始化为绘制点模式
    }

    private class SceneRenderer implements GLSurfaceView.Renderer {
        PointsOrLines PointsOrLines;//点或线

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES20.glClearColor(0, 0, 0, 1.0f);
            //创建点或线对象
            PointsOrLines = new PointsOrLines(MySurfaceView.this);
            //打开深度检测
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //打开背面剪裁
            GLES20.glEnable(GLES20.GL_CULL_FACE);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            //清除深度缓冲与颜色缓冲
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            //保护现场
            MatrixState.pushMatrix();
            //绘制原点或线
            MatrixState.pushMatrix();
            PointsOrLines.drawSelf();
            MatrixState.popMatrix();
            //恢复现场
            MatrixState.popMatrix();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            Constant.ratio = (float) width / height;
            MatrixState.setProjectFrustum(-Constant.ratio, Constant.ratio, -1, 1, 20, 100);
            MatrixState.setCamera(0, 8f, 30, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            MatrixState.setInitStack();
        }
    }
}
