package com.bn.Sample6_2;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class MySurfaceView extends GLSurfaceView {
    private static final float TOUCH_SCALE_FACTOR = 180.0f / 320;//�Ƕ����ű���

    private SceneRenderer mRenderer; //������Ⱦ��
    Ball ball;//��
    float lightOffset = -4; //�ƹ��λ�û����ƫ����
    private float mPreviousY; //�ϴεĴ���λ��Y����
    private float mPreviousX; //�ϴεĴ���λ��X����

    public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2); //����ʹ��OPENGL ES2.0
        mRenderer = new SceneRenderer();    //����������Ⱦ��
        setRenderer(mRenderer);                //������Ⱦ��
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//������ȾģʽΪ������Ⱦ   
    }

    //�����¼��ص�����
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousY;//���㴥�ر�Yλ��
                float dx = x - mPreviousX;//���㴥�ر�Xλ��
                ball.yAngle += dx * TOUCH_SCALE_FACTOR;//���������Բ��y����ת�ĽǶ�
                ball.xAngle += dy * TOUCH_SCALE_FACTOR;//���������Բ��x����ת�ĽǶ�
        }
        mPreviousY = y;//��¼���ر�λ��
        mPreviousX = x;//��¼���ر�λ��
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
