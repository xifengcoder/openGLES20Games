package com.example.opengl.learn.coordinate;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class CoordinateSystemsActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        CoordinateSystemSurfaceView surfaceView = new CoordinateSystemSurfaceView(this);
        surfaceView.requestFocus();
        surfaceView.setFocusableInTouchMode(true);
        setContentView(surfaceView);
    }
}
