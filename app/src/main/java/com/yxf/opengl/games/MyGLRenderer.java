package com.yxf.opengl.games;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.yxf.opengl.games.utils.GLUtils;
import com.yxf.opengl.games.utils.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private static final int FLOAT_BYTES = 4;
    private static final float ROTATE_SPEED = 1.0f / 10;

    private static final float[] CUBE = {
            // positions          // normals           // texture coords
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,

            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,

            -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f,

            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,

            -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f,

            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f
    };

    private int glProgram;
    private int glPositionAttr;
    private int glNormalAttr;
    private int glTexCoordAttr;

    private int glTexture1Uniform;
    private int mMVPMatrixUniform;

    private final Context mContext;

    private int[] vbo;
    private int texture1 = GLUtils.TEXTURE_NONE;

    private final Bitmap mFaceBitmap;
    private final FloatBuffer verticesBuffer;
    private final float[] modelMatrix1 = new float[16];
    private final float[] modelMatrix2 = new float[16];
    private final float[] modelMatrix3 = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    public static float[] mMVPMatrix = new float[16]; //总变换矩阵

    private long initFrameDrawingTime;

    public MyGLRenderer(Context context) {
        mContext = context;
        verticesBuffer = ByteBuffer.allocateDirect(CUBE.length * FLOAT_BYTES)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(CUBE);
        verticesBuffer.position(0);
        mFaceBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.face1);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        Matrix.perspectiveM(projectionMatrix, 0, 45.0f, width / (float) height, 5f, 50.0f);
        Matrix.setLookAtM(viewMatrix, 0, 3.0f, 3.0f, 10.0f,
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        initVertexBufferObjectIfNeeded();
        initTexturesIfNeeded();
        initGLProgramIfNeeded();

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(glProgram);

        GLES20.glVertexAttribPointer(glPositionAttr, 3, GLES20.GL_FLOAT, false, 8 * FLOAT_BYTES, 0);
        GLES20.glVertexAttribPointer(glNormalAttr, 3, GLES20.GL_FLOAT, false, 8 * FLOAT_BYTES, 3 * FLOAT_BYTES);
        GLES20.glVertexAttribPointer(glTexCoordAttr, 2, GLES20.GL_FLOAT, false, 8 * FLOAT_BYTES, 6 * FLOAT_BYTES);
        GLES20.glEnableVertexAttribArray(glPositionAttr);
        GLES20.glEnableVertexAttribArray(glNormalAttr);
        GLES20.glEnableVertexAttribArray(glTexCoordAttr);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture1);
        GLES20.glUniform1i(glTexture1Uniform, 0);

        float angle = getRotationAngle();

        //Draw cube 1: Cube 1 is in the origin of world space
        Matrix.setIdentityM(modelMatrix1, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix1, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixUniform, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);

        //Draw cube 2. Rotating about y axis...
        Matrix.setIdentityM(modelMatrix2, 0);
        Matrix.translateM(modelMatrix2, 0, 0.5f, 1.0f, -1.5f);
        Matrix.rotateM(modelMatrix2, 0, angle, 0.0f, 1.0f, 0.0f);
        Matrix.scaleM(modelMatrix2, 0, 1.5f, 1.5f, 1.5f);
        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix2, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixUniform, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);

        //Draw cube 3. Rotating about z axis...
        Matrix.setIdentityM(modelMatrix3, 0);
        Matrix.translateM(modelMatrix3, 0, 1.0f, -1.0f, 1.0f);
        Matrix.rotateM(modelMatrix3, 0, angle, 0.0f, 0.0f, 1.0f);
        Matrix.scaleM(modelMatrix3, 0, 0.5f, 0.5f, 0.5f);
        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix3, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixUniform, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
    }

    public void destroy() {
        destroyGLProgramIfNeeded();
        destroyTexturesIfNeeded();
        destroyVertexBufferObjectIfNeeded();
    }

    private float getRotationAngle() {
        float angle;

        long now = System.currentTimeMillis();
        if (initFrameDrawingTime == 0L) {
            angle = 0.0f;
            initFrameDrawingTime = now;
        } else {
            long deltaTime = now - initFrameDrawingTime;
            angle = deltaTime * ROTATE_SPEED;
        }

        return angle;
    }

    private void initGLProgramIfNeeded() {
        if (glProgram == 0) {
            String vertexShaderSource = TextResourceReader
                    .readTextFileFromResource(mContext, R.raw.vertex_shader);
            String fragmentShaderSource = TextResourceReader
                    .readTextFileFromResource(mContext, R.raw.fragment_shader);

            glProgram = GLUtils.createGLProgram(vertexShaderSource, fragmentShaderSource);
            glPositionAttr = GLES20.glGetAttribLocation(glProgram, "position");
            glTexCoordAttr = GLES20.glGetAttribLocation(glProgram, "inputTextureCoordinate");
            glNormalAttr = GLES20.glGetAttribLocation(glProgram, "normal");

            glTexture1Uniform = GLES20.glGetUniformLocation(glProgram, "inputImageTexture1");
            mMVPMatrixUniform = GLES20.glGetUniformLocation(glProgram, "uMVPMatrix");
        }
    }

    private void destroyGLProgramIfNeeded() {
        if (glProgram != 0) {
            GLES20.glDeleteProgram(glProgram);
            glProgram = 0;
        }
    }

    private void initVertexBufferObjectIfNeeded() {
        if (vbo == null) {
            vbo = new int[1];
            GLES20.glGenBuffers(1, vbo, 0);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, verticesBuffer.remaining() * FLOAT_BYTES, verticesBuffer, GLES20.GL_STATIC_DRAW);
        }
    }

    private void destroyVertexBufferObjectIfNeeded() {
        if (vbo != null) {
            GLES20.glDeleteBuffers(1, vbo, 0);
            vbo = null;
        }
    }

    private void initTexturesIfNeeded() {
        if (texture1 == GLUtils.TEXTURE_NONE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            texture1 = GLUtils.loadTextureFromBitmap(mFaceBitmap);
        }
    }

    private void destroyTexturesIfNeeded() {
        if (texture1 != GLUtils.TEXTURE_NONE) {
            GLES20.glDeleteTextures(1, new int[]{texture1}, 0);
            texture1 = GLUtils.TEXTURE_NONE;
        }
    }
}