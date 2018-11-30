package design.zhu.com.gl20.tools;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Square {
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    // 每个顶点的坐标数
    private final int COORDS_PER_VERTEX =3;
    private float squareCoords[]={-0.5f,0.5f,0.0f,// top left
            -0.5f,-0.5f,0.0f,// bottom left
            0.5f,-0.5f,0.0f,// bottom right
            0.5f,0.5f,0.0f};// top right
    // 设置颜色，分别为red, green, blue 和alpha (opacity)
    private float color[]={0.63671875f,0.76953125f,0.22265625f,1.0f};
    private short drawOrder[]={0,1,2,0,2,3};// 顶点的绘制顺序
    private final int vertexCount = squareCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 uMVPMatrix;"+
                    "void main() {" +
                    "  gl_Position = uMVPMatrix*vPosition;" +
                    "}";
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    private int mProgram = -1;

    public Square(){
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb =ByteBuffer.allocateDirect(
        // (坐标数 * 4)
                squareCoords.length *4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // 为绘制列表初始化字节缓冲
        ByteBuffer dlb =ByteBuffer.allocateDirect(
        // (对应顺序的坐标数 * 2)short是2字节
                drawOrder.length *2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgram = GLES20.glCreateProgram();             // 创建一个空的OpenGL ES Program
        GLES20.glAttachShader(mProgram, vertexShader);   // 将vertex shader添加到program
        GLES20.glAttachShader(mProgram, fragmentShader); // 将fragment shader添加到program
        GLES20.glLinkProgram(mProgram);                  // 创建可执行的 OpenGL ES program
    }

    public void draw(float[] mvpMatrix) {
        // 将program加入OpenGL ES环境中
        // 告诉OpenGL，使用我们准备好了的shader program来渲染
        GLES20.glUseProgram(mProgram);

        // 获得形状的变换矩阵的handle
        int mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram,"uMVPMatrix");
        // 应用投影和视口变换
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle,1,false, mvpMatrix,0);

        // 获取指向vertex shader的成员vPosition的 handle
        int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // 启用一个指向三角形的顶点数组的handle
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // 准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        // 获取指向fragment shader的成员vColor的handle
        int mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        // 设置三角形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // 画三角形
        GLES20.glDrawElements(GL10.GL_TRIANGLES, drawOrder.length, GL10.GL_UNSIGNED_SHORT, drawListBuffer);

        // 禁用指向三角形的顶点数组
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    private int loadShader(int type, String shaderCode){
        // 创建一个vertex shader类型(GLES20.GL_VERTEX_SHADER)
        // 或fragment shader类型(GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);
        // 将源码添加到shader并编译之
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
