package opengl.tools.graphics;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class Ball {
    public float zoom = -3f;
    public final float maxZoom = -2f;
    public final float minZoom = -4f;
    private IntBuffer mVertexBuffer;
    private IntBuffer mNormalBuffer;
    private FloatBuffer mTextureBuffer;
    public float mAngleX = 0;
    public float mAngleY = 0;
    public float mAngleZ = 0;
    int vCount = 0;
    int textureId;

    public Ball(int scale, int textureId) {
        this.textureId = textureId;
        final int R = 10000 * scale;

        ArrayList<Integer> alVertix = new ArrayList<Integer>();
        final int angleSpan = 9;

        for (int rowAngle = -90; rowAngle <= 90; rowAngle += angleSpan) {
            for (int colAngleAngle = 0; colAngleAngle < 360; colAngleAngle += angleSpan) {
                double xozLength = R * Math.cos(Math.toRadians(rowAngle));
                int x = (int) (xozLength * Math.cos(Math.toRadians(colAngleAngle)));
                int z = (int) (xozLength * Math.sin(Math.toRadians(colAngleAngle)));
                int y = (int) (R * Math.sin(Math.toRadians(rowAngle)));
                alVertix.add(x);
                alVertix.add(y);
                alVertix.add(z);
            }
        }
        vCount = alVertix.size() / 3;
        int vertices[] = new int[vCount * 3];
        for (int i = 0; i < alVertix.size(); i++) {
            vertices[i] = alVertix.get(i);
        }
        alVertix.clear();
        ArrayList<Float> alTexture = new ArrayList<Float>();

        int row = (180 / angleSpan) + 1;
        int col = 360 / angleSpan;
        float splitRow = row;
        float splitCol = col;
        for (int i = 0; i < row; i++) {
            if (i > 0 && i < row - 1) {
                for (int j = 0; j < col; j++) {
                    int k = i * col + j;
                    alVertix.add(vertices[(k + col) * 3]);
                    alVertix.add(vertices[(k + col) * 3 + 1]);
                    alVertix.add(vertices[(k + col) * 3 + 2]);

                    alTexture.add(j / splitCol);
                    alTexture.add((i + 1) / splitRow);

                    int tmp = k + 1;
                    if (j == col - 1) {
                        tmp = (i) * col;
                    }
                    alVertix.add(vertices[(tmp) * 3]);
                    alVertix.add(vertices[(tmp) * 3 + 1]);
                    alVertix.add(vertices[(tmp) * 3 + 2]);

                    alTexture.add((j + 1) / splitCol);
                    alTexture.add(i / splitRow);

                    alVertix.add(vertices[k * 3]);
                    alVertix.add(vertices[k * 3 + 1]);
                    alVertix.add(vertices[k * 3 + 2]);

                    alTexture.add(j / splitCol);
                    alTexture.add(i / splitRow);
                }
                for (int j = 0; j < col; j++) {
                    int k = i * col + j;

                    alVertix.add(vertices[(k - col) * 3]);
                    alVertix.add(vertices[(k - col) * 3 + 1]);
                    alVertix.add(vertices[(k - col) * 3 + 2]);
                    alTexture.add(j / 40f);
                    alTexture.add((i - 1) / splitRow);

                    int tmp = k - 1;
                    if (j == 0) {
                        tmp = i * col + col - 1;
                    }

                    alVertix.add(vertices[(tmp) * 3]);
                    alVertix.add(vertices[(tmp) * 3 + 1]);
                    alVertix.add(vertices[(tmp) * 3 + 2]);
                    alTexture.add((j - 1) / splitCol);
                    alTexture.add(i / splitRow);

                    alVertix.add(vertices[k * 3]);
                    alVertix.add(vertices[k * 3 + 1]);
                    alVertix.add(vertices[k * 3 + 2]);
                    alTexture.add(j / splitCol);
                    alTexture.add(i / splitRow);
                }
            }
        }

        vCount = alVertix.size() / 3;
        vertices = new int[vCount * 3];
        for (int i = 0; i < alVertix.size(); i++) {
            vertices[i] = alVertix.get(i);
        }

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asIntBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        ByteBuffer nbb = ByteBuffer.allocateDirect(vertices.length * 4);
        nbb.order(ByteOrder.nativeOrder());
        mNormalBuffer = vbb.asIntBuffer();
        mNormalBuffer.put(vertices);
        mNormalBuffer.position(0);

        float textureCoors[] = new float[alTexture.size()];
        for (int i = 0; i < alTexture.size(); i++) {
            textureCoors[i] = alTexture.get(i);
        }

        ByteBuffer cbb = ByteBuffer.allocateDirect(textureCoors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        mTextureBuffer = cbb.asFloatBuffer();
        mTextureBuffer.put(textureCoors);
        mTextureBuffer.position(0);
    }

    public void drawSelf(GL10 gl) {
        gl.glLoadIdentity();
        gl.glFrontFace(GL10.GL_CCW);

        gl.glTranslatef(0f, 0f, zoom);
        gl.glEnable(GL10.GL_CULL_FACE);

        gl.glCullFace(GL10.GL_BACK);

//        gl.glPopMatrix();
        float[] modelview = new float[16];
        ((GL11) gl).glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelview, 0);
        float[] x_axis = { 1, 0, 0, 0 };
        float[] y_axis = { 0, 1, 0, 0 };
        Matrix.invertM(modelview, 0, modelview, 0);
        Matrix.multiplyMV(x_axis, 0, modelview, 0, x_axis, 0);
        Matrix.multiplyMV(y_axis, 0, modelview, 0, y_axis, 0);

        gl.glRotatef(mAngleX, y_axis[0], y_axis[1], y_axis[2]);
//        gl.glRotatef(mAngleY, x_axis[0], x_axis[1], x_axis[2]);
        gl.glRotatef(mAngleZ, 0, 0, 1);// ??Z?????
//        gl.glPushMatrix();
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FIXED, 0, mVertexBuffer);

        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glNormalPointer(GL10.GL_FIXED, 0, mNormalBuffer);

        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, vCount);
    }

    public void cutSpeed() {
        float speed = 5f;
        mAngleX -= speed;
        mAngleY -= speed;
        mAngleZ -= speed;
        if (mAngleX < 0)
            mAngleX = 0;
        if (mAngleY < 0)
            mAngleY = 0;
        if (mAngleZ < 0)
            mAngleZ = 0;
    }
}
