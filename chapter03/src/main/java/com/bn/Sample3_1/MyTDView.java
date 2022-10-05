package com.bn.Sample3_1;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyTDView extends GLSurfaceView {
    private TriangleRender mRenderer;

    public MyTDView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2);
        mRenderer = new TriangleRender(this);
        this.setRenderer(mRenderer);
        this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
}