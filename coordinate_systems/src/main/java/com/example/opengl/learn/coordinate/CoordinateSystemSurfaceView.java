package com.example.opengl.learn.coordinate;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.example.opengl.learn.R;
import com.yxf.opengl.common.MatrixState;
import com.yxf.opengl.common.utils.TextureUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CoordinateSystemSurfaceView extends GLSurfaceView {

    private Context mContext;
    private CoordinateSystemsRender mRenderer;//场景渲染器
    private CoordinateSystem mCoordinateSystem;

    public CoordinateSystemSurfaceView(Context context) {
        super(context);
        mContext = context;
        this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        mRenderer = new CoordinateSystemsRender();    //创建场景渲染器
        setRenderer(mRenderer);                //设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染
    }

    private class CoordinateSystemsRender implements GLSurfaceView.Renderer {

        private int mTextureId1;
        private int mTextureId2;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.5f);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            mTextureId1 = TextureUtil.initTexture(mContext, R.drawable.wooden_container);
            mTextureId2 = TextureUtil.initTexture(mContext, R.drawable.awesomeface);
            mCoordinateSystem = new CoordinateSystem(CoordinateSystemSurfaceView.this);
            MatrixState.setInitStack();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
        }
        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GL_COLOR_BUFFER_BIT);
            MatrixState.pushMatrix();
            //MatrixState.rotate(-55.0f, 1, 0, 0);
            mCoordinateSystem.drawSelf(mTextureId1, mTextureId2);
            MatrixState.popMatrix();
        }
    }
}