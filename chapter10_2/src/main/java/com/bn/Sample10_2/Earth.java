package com.bn.Sample10_2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.opengl.GLES20;

import com.yxf.opengl.common.utils.ShaderUtil;

//表示地球的类，采用多重纹理
public class Earth {
    int mProgram;//自定义渲染管线程序id
    int muMVPMatrixHandle;//总变换矩阵引用id
    int muMMatrixHandle;//位置、旋转变换矩阵
    int maCameraHandle; //摄像机位置属性引用id
    int maPositionHandle; //顶点位置属性引用id
    int maNormalHandle; //顶点法向量属性引用id
    int maTexCoorHandle; //顶点纹理坐标属性引用id
    int maSunLightLocationHandle;//光源位置属性引用id
    int uDayTexHandle;//白天纹理属性引用id
    int uNightTexHandle;//黑夜纹理属性引用id
    String mVertexShader;//顶点着色器
    String mFragmentShader;//片元着色器
    FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    FloatBuffer mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int vCount = 0;

    public Earth(MySurfaceView mv, float r) {
        //初始化顶点坐标与着色数据
        initVertexData(r);
        //初始化着色器
        initShader(mv);
    }

    //初始化顶点坐标与纹理数据的方法
    public void initVertexData(float r) {
        //顶点坐标数据的初始化================begin============================
        final float UNIT_SIZE = 0.5f;
        ArrayList<Float> alVertix = new ArrayList<Float>();//存放顶点坐标的ArrayList
        final float angleSpan = 10f;//将球进行单位切分的角度
        for (float vAngle = 90; vAngle > -90; vAngle = vAngle - angleSpan) {//垂直方向angleSpan度一份
            for (float hAngle = 360; hAngle > 0; hAngle = hAngle - angleSpan) {//水平方向angleSpan度一份
                //纵向横向各到一个角度后计算对应的此点在球面上的坐标
                double xozLength = r * UNIT_SIZE * Math.cos(Math.toRadians(vAngle));
                float x1 = (float) (xozLength * Math.cos(Math.toRadians(hAngle)));
                float z1 = (float) (xozLength * Math.sin(Math.toRadians(hAngle)));
                float y1 = (float) (r * UNIT_SIZE * Math.sin(Math.toRadians(vAngle)));
                xozLength = r * UNIT_SIZE * Math.cos(Math.toRadians(vAngle - angleSpan));
                float x2 = (float) (xozLength * Math.cos(Math.toRadians(hAngle)));
                float z2 = (float) (xozLength * Math.sin(Math.toRadians(hAngle)));
                float y2 = (float) (r * UNIT_SIZE * Math.sin(Math.toRadians(vAngle - angleSpan)));
                xozLength = r * UNIT_SIZE * Math.cos(Math.toRadians(vAngle - angleSpan));
                float x3 = (float) (xozLength * Math.cos(Math.toRadians(hAngle - angleSpan)));
                float z3 = (float) (xozLength * Math.sin(Math.toRadians(hAngle - angleSpan)));
                float y3 = (float) (r * UNIT_SIZE * Math.sin(Math.toRadians(vAngle - angleSpan)));
                xozLength = r * UNIT_SIZE * Math.cos(Math.toRadians(vAngle));
                float x4 = (float) (xozLength * Math.cos(Math.toRadians(hAngle - angleSpan)));
                float z4 = (float) (xozLength * Math.sin(Math.toRadians(hAngle - angleSpan)));
                float y4 = (float) (r * UNIT_SIZE * Math.sin(Math.toRadians(vAngle)));
                //构建第一三角形
                alVertix.add(x1);
                alVertix.add(y1);
                alVertix.add(z1);
                alVertix.add(x2);
                alVertix.add(y2);
                alVertix.add(z2);
                alVertix.add(x4);
                alVertix.add(y4);
                alVertix.add(z4);
                //构建第二三角形
                alVertix.add(x4);
                alVertix.add(y4);
                alVertix.add(z4);
                alVertix.add(x2);
                alVertix.add(y2);
                alVertix.add(z2);
                alVertix.add(x3);
                alVertix.add(y3);
                alVertix.add(z3);
            }
        }
        vCount = alVertix.size() / 3;//顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标
        //将alVertix中的坐标值转存到一个float数组中
        float vertices[] = new float[vCount * 3];
        for (int i = 0; i < alVertix.size(); i++) {
            vertices[i] = alVertix.get(i);
        }
        //创建顶点坐标数据缓冲
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为int型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //将alTexCoor中的纹理坐标值转存到一个float数组中
        float[] texCoor = generateTexCoor(//获取切分整图的纹理数组
                (int) (360 / angleSpan), //纹理图切分的列数
                (int) (180 / angleSpan)  //纹理图切分的行数
        );
        ByteBuffer llbb = ByteBuffer.allocateDirect(texCoor.length * 4);
        llbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTexCoorBuffer = llbb.asFloatBuffer();
        mTexCoorBuffer.put(texCoor);
        mTexCoorBuffer.position(0);
    }

    public void initShader(MySurfaceView mv) { //初始化着色器
        //加载顶点着色器的脚本内容
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex_earth.sh", mv.getResources());
        ShaderUtil.checkGlError("==ss==");
        //加载片元着色器的脚本内容
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag_earth.sh", mv.getResources());
        //基于顶点着色器与片元着色器创建程序
        ShaderUtil.checkGlError("==ss==");
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用id
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理属性引用id
        maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中顶点法向量属性引用id
        maNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取程序中摄像机位置引用id
        maCameraHandle = GLES20.glGetUniformLocation(mProgram, "uCamera");
        //获取程序中光源位置引用id
        maSunLightLocationHandle = GLES20.glGetUniformLocation(mProgram, "uLightLocationSun");
        //获取白天、黑夜两个纹理引用
        uDayTexHandle = GLES20.glGetUniformLocation(mProgram, "sTextureDay");
        uNightTexHandle = GLES20.glGetUniformLocation(mProgram, "sTextureNight");
        //获取位置、旋转变换矩阵引用id
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");
    }

    public void drawSelf(int texId, int texIdNight) {
        //制定使用某套着色器程序
        GLES20.glUseProgram(mProgram);
        //将最终变换矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        //将位置、旋转变换矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        //将摄像机位置传入着色器程序
        GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
        //将光源位置传入着色器程序
        GLES20.glUniform3fv(maSunLightLocationHandle, 1, MatrixState.lightPositionFBSun);
        GLES20.glVertexAttribPointer(//为画笔指定顶点位置数据
                maPositionHandle,
                3,
                GLES20.GL_FLOAT,
                false,
                3 * 4,
                mVertexBuffer
        );
        GLES20.glVertexAttribPointer(  //为画笔指定顶点纹理数据
                maTexCoorHandle,
                2,
                GLES20.GL_FLOAT,
                false,
                2 * 4,
                mTexCoorBuffer
        );
        GLES20.glVertexAttribPointer    //为画笔指定顶点法向量数据
                (
                        maNormalHandle,
                        4,
                        GLES20.GL_FLOAT,
                        false,
                        3 * 4,
                        mVertexBuffer
                );
        //允许顶点位置数据数组
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);
        GLES20.glEnableVertexAttribArray(maNormalHandle);
        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texIdNight);
        GLES20.glUniform1i(uDayTexHandle, 0);
        GLES20.glUniform1i(uNightTexHandle, 1);
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
    }

    //自动切分纹理产生纹理数组的方法
    public float[] generateTexCoor(int bw, int bh) {
        float[] result = new float[bw * bh * 6 * 2];
        float sizew = 1.0f / bw;//列数
        float sizeh = 1.0f / bh;//行数
        int c = 0;
        for (int i = 0; i < bh; i++) {
            for (int j = 0; j < bw; j++) {
                //每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
                float s = j * sizew;
                float t = i * sizeh;
                result[c++] = s;
                result[c++] = t;
                result[c++] = s;
                result[c++] = t + sizeh;
                result[c++] = s + sizew;
                result[c++] = t;
                result[c++] = s + sizew;
                result[c++] = t;
                result[c++] = s;
                result[c++] = t + sizeh;
                result[c++] = s + sizew;
                result[c++] = t + sizeh;
            }
        }
        return result;
    }
}
