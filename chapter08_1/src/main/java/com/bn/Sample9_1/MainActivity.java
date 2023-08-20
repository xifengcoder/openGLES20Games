package com.bn.Sample9_1;


import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;


public class MainActivity extends Activity {
    private MySurfaceView mySurfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        //初始化GLSurfaceView
        mySurfaceView = new MySurfaceView(this);
        mySurfaceView.requestFocus();//获取焦点
        mySurfaceView.setFocusableInTouchMode(true);//设置为可触控
        //将自定义的GLSurfaceView添加到外层LinearLayout中
        LinearLayout ll = (LinearLayout) findViewById(R.id.main_liner);
        ll.addView(mySurfaceView);

        //为RadioButton添加监听器及SxT选择代码
        RadioButton rab = (RadioButton) findViewById(R.id.RadioButton01);
        rab.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //面填充
                if (isChecked) {
                    mySurfaceView.drawWhatFlag = true;
                }
            }
        });
        rab = (RadioButton) findViewById(R.id.RadioButton02);
        rab.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //线填充
                if (isChecked) {
                    mySurfaceView.drawWhatFlag = false;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mySurfaceView.onResume();
        mySurfaceView.lightFlag = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mySurfaceView.onPause();
        mySurfaceView.lightFlag = false;
    }


    public boolean onKeyDown(int keyCode, KeyEvent e) {
        switch (keyCode) {
            case 4:
                System.exit(0);
                break;
        }
        return true;
    }
}