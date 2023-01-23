package com.bn.Sample7_2;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;

public class MyActivity extends Activity {
    private MySurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mGLSurfaceView = new MySurfaceView(this);
        mGLSurfaceView.requestFocus();//获取焦点
        mGLSurfaceView.setFocusableInTouchMode(true);//设置为可触控
        LinearLayout ll = (LinearLayout) findViewById(R.id.main_liner);
        ll.addView(mGLSurfaceView);

        RadioButton rab = (RadioButton) findViewById(R.id.edge);
        rab.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    //GL_CLAMP_TO_EDGE模式下
                    if (isChecked) {
                        mGLSurfaceView.currTextureId = mGLSurfaceView.textureCTId;
                    }
                }
        );
        rab = (RadioButton) findViewById(R.id.repeat);
        rab.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    //GL_REPEAT模式下
                    if (isChecked) {
                        mGLSurfaceView.currTextureId = mGLSurfaceView.textureREId;
                    }
                }
        );
        //为RadioButton添加监听器及SxT选择代码
        RadioButton rb = (RadioButton) findViewById(R.id.x11);
        rb.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (isChecked) {//设置为纹理坐标SxT=1x1
                        mGLSurfaceView.trIndex = 0;
                    }
                }
        );
        rb = (RadioButton) findViewById(R.id.x42);
        rb.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (isChecked) {//设置为纹理坐标SxT=4x2
                        mGLSurfaceView.trIndex = 1;
                    }
                }
        );
        rb = (RadioButton) findViewById(R.id.x44);
        rb.setOnCheckedChangeListener(
                new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {//设置为纹理坐标SxT=4x4
                            mGLSurfaceView.trIndex = 2;
                        }
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }
}