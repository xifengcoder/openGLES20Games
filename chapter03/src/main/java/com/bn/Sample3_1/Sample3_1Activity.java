package com.bn.Sample3_1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class Sample3_1Activity extends Activity {
    MyTDView myTDView;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        myTDView = new MyTDView(this);
        myTDView.requestFocus();
        myTDView.setFocusableInTouchMode(true);
        setContentView(myTDView);
    }

    @Override
    public void onResume() {
        super.onResume();
        myTDView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        myTDView.onPause();
    }
}