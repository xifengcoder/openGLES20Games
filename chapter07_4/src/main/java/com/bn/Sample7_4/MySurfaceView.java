package com.bn.Sample7_4;

import static com.bn.Sample7_4.Constant.ratio;
import static com.bn.Sample7_4.Constant.threadFlag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class MySurfaceView extends GLSurfaceView {
    private static final float TOUCH_SCALE_FACTOR = 180.0f / 320;//角度缩放比例
    private SceneRenderer mRenderer;//场景渲染器

    private float mPreviousX;//上次的触控位置X坐标
    private float mPreviousY;//上次的触控位置Y坐标

    int textureIdEarth;//系统分配的地球纹理id
    int textureIdEarthNight;//系统分配的地球夜晚纹理id
    int textureIdMoon;//系统分配的月球纹理id

    float yAngle = 0;//太阳灯光绕y轴旋转的角度
    float xAngle = 0;//摄像机绕X轴旋转的角度

    float eAngle = 0;//地球自转角度
    float cAngle = 0;//天球自转的角度

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
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //触控横向位移太阳绕y轴旋转
                float dx = x - mPreviousX;//计算触控笔X位移
                yAngle += dx * TOUCH_SCALE_FACTOR;//设置太阳绕y轴旋转的角度
                float sunX = (float) (Math.cos(Math.toRadians(yAngle)) * 100);
                float sunZ = -(float) (Math.sin(Math.toRadians(yAngle)) * 100);
                MatrixState.setLightLocationSun(sunX, 5, sunZ);

                //触控纵向位移摄像机绕x轴旋转 -90～+90
                float dy = y - mPreviousY;//计算触控笔Y位移
                xAngle += dy * TOUCH_SCALE_FACTOR;//设置太阳绕y轴旋转的角度
                if (xAngle > 90) {
                    xAngle = 90;
                } else if (xAngle < -90) {
                    xAngle = -90;
                }
                float cy = (float) (7.2 * Math.sin(Math.toRadians(xAngle)));
                float cz = (float) (7.2 * Math.cos(Math.toRadians(xAngle)));
                float upy = (float) Math.cos(Math.toRadians(xAngle));
                float upz = -(float) Math.sin(Math.toRadians(xAngle));
                MatrixState.setCamera(0, cy, cz, 0, 0, 0, 0, upy, upz);
        }
        mPreviousX = x;//记录触控笔位置
        mPreviousY = y;
        return true;
    }

    private class SceneRenderer implements GLSurfaceView.Renderer {
        Earth earth;//地球
        Moon moon;//月球
        Celestial cSmall;//小星星天球
        Celestial cBig;//大星星天球

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            earth = new Earth(MySurfaceView.this, 2.0f);
            moon = new Moon(MySurfaceView.this, 1.0f);
            cSmall = new Celestial(1, 0, 1000, MySurfaceView.this);
            cBig = new Celestial(2, 0, 500, MySurfaceView.this);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            MatrixState.setInitStack();
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            MatrixState.pushMatrix();
            MatrixState.rotate(eAngle, 0, 1, 0); //地球自转
            earth.drawSelf(textureIdEarth, textureIdEarthNight); //绘制纹理圆球
            MatrixState.transtate(2f, 0, 0); //推坐标系到月球位置
            MatrixState.rotate(eAngle, 0, 1, 0); //月球自转
            moon.drawSelf(textureIdMoon); //绘制月球
            MatrixState.popMatrix();  //恢复现场

            MatrixState.pushMatrix();
            MatrixState.rotate(cAngle, 0, 1, 0);
            cSmall.drawSelf();
            cBig.drawSelf();
            MatrixState.popMatrix();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            ratio = (float) width / height;
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 4f, 100);
            MatrixState.setCamera(0, 0, 7.2f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            textureIdEarth = initTexture(R.drawable.earth);
            textureIdEarthNight = initTexture(R.drawable.earthn);
            textureIdMoon = initTexture(R.drawable.moon);
            //设置太阳灯光的初始位置
            MatrixState.setLightLocationSun(200, 5, 0);

            //启动一个线程定时旋转地球、月球
            new Thread() {
                public void run() {
                    while (threadFlag) {
                        eAngle = (eAngle + 2) % 360;
                        cAngle = (cAngle + 0.2f) % 360;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
    }

    public int initTexture(int drawableId) {
        //生成纹理ID
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        int textureId = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        //通过输入流加载图片===============begin===================
        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try {
            bitmapTmp = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //通过输入流加载图片===============end=====================

        //实际加载纹理
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmapTmp, 0);
        bitmapTmp.recycle();          //纹理加载成功后释放图片

        return textureId;
    }
}
