package com.yxf.opengl.games;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private GLSurfaceView glSurfaceView;
    private MyGLRenderer myGLRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        glSurfaceView = (GLSurfaceView) findViewById(R.id.surface);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        myGLRenderer = new MyGLRenderer(this);
        glSurfaceView.setRenderer(myGLRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                /**
                 * Generally, GL Context will be destroyed after pause.
                 * So we destroy GL-related resources before pause.
                 */
                myGLRenderer.destroy();
            }
        });
        glSurfaceView.onPause();
    }
}
