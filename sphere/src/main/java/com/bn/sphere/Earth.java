package com.bn.sphere;

import android.opengl.GLES20;
import android.util.Log;

import com.yxf.opengl.common.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Earth {
    private static final float ANGLE_SPAN = 10f;
    private static final float UNIT_SIZE = 0.5f;
    private static final String TAG = "Earth";

    float yAngle = 0;// 绕y轴旋转的角度
    float xAngle = 0;// 绕x轴旋转的角度

    int mProgram;
    int muMVPMatrixHandle;
    int muMMatrixHandle;
    int maCameraHandle;
    int maPositionHandle;
    int maNormalHandle;
    int maTexCoorHandle;
    int maSunLightLocationHandle;
    int uDayTexHandle;
    int uNightTexHandle;
    String mVertexShader;
    String mFragmentShader;
    FloatBuffer mVertexBuffer;
    FloatBuffer mTexCoorBuffer;
    int vCount = 0;

    public Earth(MySurfaceView mySurfaceView, float v) {
        initVertexData(v);
        initShader(mySurfaceView);
    }

    public void initShader(MySurfaceView mv) {
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex_earth.sh", mv.getResources());
        ShaderUtil.checkGlError("==ss==");
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag_earth.sh", mv.getResources());
        ShaderUtil.checkGlError("==ss==");

        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        maNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        maCameraHandle = GLES20.glGetUniformLocation(mProgram, "uCamera");
        maSunLightLocationHandle = GLES20.glGetUniformLocation(mProgram, "uLightLocationSun");
        uDayTexHandle = GLES20.glGetUniformLocation(mProgram, "sTextureDay");
        uNightTexHandle = GLES20.glGetUniformLocation(mProgram, "sTextureNight");
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");
    }

    public void initVertexData(float radius) {
        ArrayList<Float> allVertices = new ArrayList<Float>();
        for (float vAngle = 90; vAngle > -90; vAngle = vAngle - ANGLE_SPAN) {//垂直方向angleSpan度一份
            for (float hAngle = 360; hAngle > 0; hAngle = hAngle - ANGLE_SPAN) {//水平方向angleSpan度一份
                //纵向横向各到一个角度后计算对应的此点在球面上的坐标
                double xozLength = radius * UNIT_SIZE * Math.cos(Math.toRadians(vAngle));
                float x1 = (float) (xozLength * Math.cos(Math.toRadians(hAngle)));
                float z1 = (float) (xozLength * Math.sin(Math.toRadians(hAngle)));
                float y1 = (float) (radius * UNIT_SIZE * Math.sin(Math.toRadians(vAngle)));

                xozLength = radius * UNIT_SIZE * Math.cos(Math.toRadians(vAngle - ANGLE_SPAN));
                float x2 = (float) (xozLength * Math.cos(Math.toRadians(hAngle)));
                float z2 = (float) (xozLength * Math.sin(Math.toRadians(hAngle)));
                float y2 = (float) (radius * UNIT_SIZE * Math.sin(Math.toRadians(vAngle - ANGLE_SPAN)));

                xozLength = radius * UNIT_SIZE * Math.cos(Math.toRadians(vAngle - ANGLE_SPAN));
                float x3 = (float) (xozLength * Math.cos(Math.toRadians(hAngle - ANGLE_SPAN)));
                float z3 = (float) (xozLength * Math.sin(Math.toRadians(hAngle - ANGLE_SPAN)));
                float y3 = (float) (radius * UNIT_SIZE * Math.sin(Math.toRadians(vAngle - ANGLE_SPAN)));

                xozLength = radius * UNIT_SIZE * Math.cos(Math.toRadians(vAngle));
                float x4 = (float) (xozLength * Math.cos(Math.toRadians(hAngle - ANGLE_SPAN)));
                float z4 = (float) (xozLength * Math.sin(Math.toRadians(hAngle - ANGLE_SPAN)));
                float y4 = (float) (radius * UNIT_SIZE * Math.sin(Math.toRadians(vAngle)));

                //构建第一三角形
                allVertices.add(x1);
                allVertices.add(y1);
                allVertices.add(z1);

                allVertices.add(x2);
                allVertices.add(y2);
                allVertices.add(z2);

                allVertices.add(x4);
                allVertices.add(y4);
                allVertices.add(z4);

                //构建第2个三角形
                allVertices.add(x4);
                allVertices.add(y4);
                allVertices.add(z4);

                allVertices.add(x2);
                allVertices.add(y2);
                allVertices.add(z2);

                allVertices.add(x3);
                allVertices.add(y3);
                allVertices.add(z3);
            }
        }
        vCount = allVertices.size() / 3;
        float[] vertices = new float[vCount * 3];
        for (int i = 0; i < allVertices.size(); i++) {
            vertices[i] = allVertices.get(i);
        }
        mVertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertices);
        mVertexBuffer.position(0);

        float[] texCoor = generateTexCoor((int) (360 / ANGLE_SPAN), (int) (180 / ANGLE_SPAN));
        Log.i(TAG, "vertices.size: " + vertices.length + ", texCoor.len: " + texCoor.length);
        mTexCoorBuffer = ByteBuffer.allocateDirect(texCoor.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(texCoor);
        mTexCoorBuffer.position(0);
    }

    public void drawSelf(int texId, int texIdNight) {
        MatrixState.rotate(xAngle, 1, 0, 0);
        MatrixState.rotate(yAngle, 0, 1, 0);
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
        GLES20.glUniform3fv(maSunLightLocationHandle, 1, MatrixState.lightPositionFBSun);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, mVertexBuffer);
        GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT,
                false, 2 * 4, mTexCoorBuffer);
        GLES20.glVertexAttribPointer(maNormalHandle, 4, GLES20.GL_FLOAT,
                false, 3 * 4, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);
        GLES20.glEnableVertexAttribArray(maNormalHandle);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texIdNight);
        GLES20.glUniform1i(uDayTexHandle, 0);
        GLES20.glUniform1i(uNightTexHandle, 1);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
    }

    /**
     * `    2311q
     * @param columnSliceNum
     * @param rowSliceNum
     * @return
     */
    public float[] generateTexCoor(int columnSliceNum, int rowSliceNum) {
        float[] result = new float[columnSliceNum * rowSliceNum * 6 * 2];
        float columnStep = 1.0f / columnSliceNum;
        float rowStep = 1.0f / rowSliceNum;
        int index = 0;
        for (int i = 0; i < rowSliceNum; i++) {
            for (int j = 0; j < columnSliceNum; j++) {
                //每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
                float s = j * columnStep;
                float t = i * rowStep;

                result[index++] = s;
                result[index++] = t;

                result[index++] = s;
                result[index++] = t + rowStep;

                result[index++] = s + columnStep;
                result[index++] = t;

                result[index++] = s + columnStep;
                result[index++] = t;

                result[index++] = s;
                result[index++] = t + rowStep;

                result[index++] = s + columnStep;
                result[index++] = t + rowStep;
            }
        }
        return result;
    }
}
