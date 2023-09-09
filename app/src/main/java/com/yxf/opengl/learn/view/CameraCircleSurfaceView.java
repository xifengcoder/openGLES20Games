package com.yxf.opengl.learn.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.yxf.opengl.learn.CameraCircleRender;

public class CameraCircleSurfaceView extends GLSurfaceView {

    private CameraCircleRender cameraCircleRender;

    public CameraCircleSurfaceView(Context context) {
        this(context, null);
    }

    public CameraCircleSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(3);
        cameraCircleRender = new CameraCircleRender(context);
        setRenderer(cameraCircleRender);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }
}
