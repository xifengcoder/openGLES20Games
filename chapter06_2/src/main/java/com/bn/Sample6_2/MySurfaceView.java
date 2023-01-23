package com.bn.Sample6_2;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class MySurfaceView extends GLSurfaceView {
    private static final float TOUCH_SCALE_FACTOR = 180.0f / 320;//角度缩放比例

    private SceneRenderer mRenderer; //场景渲染器
    Ball ball;//球
    float lightOffset = -4; //灯光的位置或方向的偏移量
    private float mPreviousY; //上次的触控位置Y坐标
    private float mPreviousX; //上次的触控位置X坐标

    public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        mRenderer = new SceneRenderer();    //创建场景渲染器
        setRenderer(mRenderer);                //设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }

    //触摸事件回调方法
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousY;//计算触控笔Y位移
                float dx = x - mPreviousX;//计算触控笔X位移
                ball.yAngle += dx * TOUCH_SCALE_FACTOR;//设置填充椭圆绕y轴旋转的角度
                ball.xAngle += dy * TOUCH_SCALE_FACTOR;//设置填充椭圆绕x轴旋转的角度
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }

    private class SceneRenderer implements Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0f, 0f, 0f, 1.0f);
            ball = new Ball(MySurfaceView.this);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glEnable(GLES20.GL_CULL_FACE);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            float ratio = (float) width / height;
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 20, 100);
            MatrixState.setCamera(0, 0f, 30, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            MatrixState.setInitStack();
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            MatrixState.pushMatrix();
            MatrixState.pushMatrix();
            MatrixState.translate(-2.2f, 0, 0);
            ball.drawSelf();
            MatrixState.popMatrix();
            MatrixState.pushMatrix();
            MatrixState.translate(2.2f, 0, 0);
            ball.drawSelf();
            MatrixState.popMatrix();
            MatrixState.popMatrix();
        }
    }

    public void setLightOffset(float lightOffset) {
        this.lightOffset = lightOffset;
    }
}
