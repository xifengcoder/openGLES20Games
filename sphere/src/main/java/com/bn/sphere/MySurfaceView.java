package com.bn.sphere;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.yxf.opengl.common.utils.TextureUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MySurfaceView extends GLSurfaceView {
    private SceneRenderer mRenderer;

    Earth earth;//地球
    private int textureIdEarth;
    private int textureIdEarthNight;

    float eAngle = 0;//地球自转角度

    public MySurfaceView(Context context) {
        super(context);
        init();
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        mRenderer = new SceneRenderer();    //创建场景渲染器
        setRenderer(mRenderer);                //设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染
    }

    private class SceneRenderer implements GLSurfaceView.Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            earth = new Earth(MySurfaceView.this, 2.0f);
            MatrixState.setInitStack();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            float ratio = (float) width / height;
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 4f, 100);
            MatrixState.setCamera(0, 0, 7.2f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            textureIdEarth = TextureUtil.initTexture(MySurfaceView.this.getContext(), R.drawable.earth);
            textureIdEarthNight = TextureUtil.initTexture(MySurfaceView.this.getContext(), R.drawable.earthn);
            //设置太阳灯光的初始位置
            MatrixState.setLightLocationSun(200, 5, 0);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            MatrixState.pushMatrix();
            MatrixState.rotate(eAngle, 0, 1, 0); //地球自转
            earth.drawSelf(textureIdEarth, textureIdEarthNight); //绘制纹理圆球
            MatrixState.popMatrix();  //恢复现场
        }
    }
}
