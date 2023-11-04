package com.example.opengl.learn.coordinate;

import android.opengl.GLES20;
import android.util.Log;

import com.yxf.opengl.common.MatrixState;
import com.yxf.opengl.common.utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class CoordinateSystem {
    private static final String TAG = "CoordinateSystem";
    float[] vertices = {
//            ---- 位置 ----
            0.5f, 0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
    };

    float[] coordinates = {
            // - 纹理坐标 -
            1.0f, 1.0f,  // 右上
            1.0f, 0.0f,   // 右下
            0.0f, 1.0f,    // 左上
            0.0f, 0.0f,   // 左下
            1.0f, 0.0f,   // 右下
            0.0f, 1.0f,    // 左上
    };

    private int mProgram;
    private int mPositionHandle;

    private FloatBuffer mVertexBuffer;
    private int muMVPMatrixHandle;
    private int maTexCoorHandle;
    private FloatBuffer mTexCoordBuffer;

    public CoordinateSystem(CoordinateSystemSurfaceView surfaceView) {
        initVertexData();
        initShader(surfaceView);
    }

    private void initShader(CoordinateSystemSurfaceView surfaceView) {
        String vertexShader = ShaderUtil.loadFromAssetsFile("vertex_coordinate_systems.glsl", surfaceView.getResources());
        String fragmentShader = ShaderUtil.loadFromAssetsFile("frag_coordinate_systems.glsl", surfaceView.getResources());

        mProgram = ShaderUtil.createProgram(vertexShader, fragmentShader);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    private void initVertexData() {
        mVertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertices);
        mVertexBuffer.position(0);

        mTexCoordBuffer = ByteBuffer.allocateDirect(coordinates.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(coordinates);
        mTexCoordBuffer.position(0);
    }

    public void drawSelf(int textureId1, int textureId2) {
        GLES20.glUseProgram(mProgram);

        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);

        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle); //启用顶点位置数据


        GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, mTexCoordBuffer);
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId1);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId2);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }
}
