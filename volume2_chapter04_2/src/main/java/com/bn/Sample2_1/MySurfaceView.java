package com.bn.Sample2_1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.yxf.opengl.common.MatrixState;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

@SuppressLint("NewApi")
class MySurfaceView extends GLSurfaceView {
    private static final float TOUCH_SCALE_FACTOR = 180.0f / 320;//角度缩放比例
    private SceneRenderer mRenderer;//场景渲染器

    private float mPreviousY;//上次的触控位置Y坐标
    private float mPreviousX;//上次的触控位置X坐标
    //关于摄像机的变量
    private float cameraX = 0;//摄像机x位置
    private float cameraY = 0;//摄像机y位置
    private float cameraZ = 60;//摄像机z位置

    private float targetX = 0;//目标点x位置
    private float targetY = 0;//目标点y位置
    private float targetZ = 0;//目标点z位置

    private float currSightDis = 60;//摄像机和目标的距离
    private float angdegElevation = 30;//仰角
    private float angdegAzimuth = 180;//方位角

    //关于灯光的变量
    private float lightPositionX = 0;//x位置
    private float lightPositionY = 0;//y位置
    private float lightPositionZ = 0;//z位置

    private static final float LIGHT_DISTANCE = 100;
    private static final float LIGHT_ELEVATION = 40;//灯光仰角
    private float lightAzimuth = 180;//灯光的方位角

    public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        mRenderer = new SceneRenderer();    //创建场景渲染器
        setRenderer(mRenderer);                //设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染
    }

    //触摸事件回调方法
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousY;//计算触控笔Y位移
                float dx = x - mPreviousX;//计算触控笔X位移
                angdegAzimuth += dx * TOUCH_SCALE_FACTOR;//设置沿x轴旋转角度
                angdegElevation += dy * TOUCH_SCALE_FACTOR;//设置沿z轴旋转角度
                //将仰角限制在5〜90度范围内
                angdegElevation = Math.max(angdegElevation, 5);
                angdegElevation = Math.min(angdegElevation, 90);
                //设置摄像机的位置
                setCameraPosition();
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }


    // 设置摄像机位置的方法
    public void setCameraPosition() {
        //计算摄像机的位置
        double angradElevation = Math.toRadians(angdegElevation);// 仰角（弧度）
        double angradAzimuth = Math.toRadians(angdegAzimuth);// 方位角
        cameraX = (float) (targetX - currSightDis * Math.cos(angradElevation) * Math.sin(angradAzimuth));
        cameraY = (float) (targetY + currSightDis * Math.sin(angradElevation));
        cameraZ = (float) (targetZ - currSightDis * Math.cos(angradElevation) * Math.cos(angradAzimuth));
    }

    // 位置灯光位置的方法
    public void setLightPosition() {
        //计算灯光的位置
        double angradElevation = Math.toRadians(LIGHT_ELEVATION);// 仰角（弧度）
        double angradAzimuth = Math.toRadians(lightAzimuth);// 方位角
        lightPositionX = (float) (-LIGHT_DISTANCE * Math.cos(angradElevation) * Math.sin(angradAzimuth));
        lightPositionY = (float) (+LIGHT_DISTANCE * Math.sin(angradElevation));
        lightPositionZ = (float) (-LIGHT_DISTANCE * Math.cos(angradElevation) * Math.cos(angradAzimuth));
    }

    @SuppressLint("NewApi")
    private class SceneRenderer implements Renderer {
        //从指定的obj文件中加载对象
        LoadedObjectVertexNormalFace pm;
        LoadedObjectVertexNormalFace cft;
        LoadedObjectVertexNormalAverage qt;
        LoadedObjectVertexNormalAverage yh;
        LoadedObjectVertexNormalAverage ch;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES20.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
            //打开深度检测
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //关闭背面剪裁
            GLES20.glDisable(GLES20.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();
            //加载要绘制的物体
            //茶壶
            ch = LoadUtil.loadFromFileVertexOnlyAverage("ch.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            //平面
            pm = LoadUtil.loadFromFileVertexOnlyFace("pm.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            //长方体
            cft = LoadUtil.loadFromFileVertexOnlyFace("cft.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            //球体
            qt = LoadUtil.loadFromFileVertexOnlyAverage("qt.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            //圆环
            yh = LoadUtil.loadFromFileVertexOnlyAverage("yh.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            //清除深度缓冲与颜色缓冲
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            //设置camera位置
            MatrixState.setCamera(cameraX, cameraY, cameraZ, targetX, targetY, targetZ, 0, 1, 0);
            //初始化光源位置
            MatrixState.setLightLocation(lightPositionX, lightPositionY, lightPositionZ);
            //若加载的物体不为空则绘制物体
            pm.drawSelf(0);//绘制平面
            drawObject(1);//绘制平面上各个物体的阴影
            drawObject(0);//绘制物体本身
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            float ratio = (float) width / height;
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);
            //计算摄像机的位置
            setCameraPosition();
            //计算灯光的位置
            setLightPosition();
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        lightAzimuth += 1;
                        lightAzimuth %= 360;
                        //计算灯光的位置
                        setLightPosition();
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }

        //绘制平面上物体的方法, 根据参数的不同决定绘制阴影还是物体本身
        private void drawObject(int situ) {
            //绘制长方体
            MatrixState.pushMatrix();
            MatrixState.scale(1.5f, 1.5f, 1.5f);//进行缩放变换
            MatrixState.translate(-10f, 0f, 0);//进行平移变换
            cft.drawSelf(situ);//绘制长方体
            MatrixState.popMatrix();   //恢复现场
            //绘制球体
            MatrixState.pushMatrix();
            MatrixState.scale(1.5f, 1.5f, 1.5f);
            MatrixState.translate(10f, 0f, 0);
            qt.drawSelf(situ);
            MatrixState.popMatrix();
            //绘制圆环
            MatrixState.pushMatrix();
            MatrixState.scale(1.5f, 1.5f, 1.5f);
            MatrixState.translate(0, 0, -10f);
            yh.drawSelf(situ);
            MatrixState.popMatrix();
            //绘制茶壶
            MatrixState.pushMatrix();
            MatrixState.scale(1.5f, 1.5f, 1.5f);
            MatrixState.translate(0, 0, 10f);
            ch.drawSelf(situ);
            MatrixState.popMatrix();
        }
    }
}
