package design.zhu.com.gl20.tools;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {
    private FloatBuffer vertexBuffer;
    // 数组中每个顶点的坐标数
    private final int COORDS_PER_VERTEX =3;
    private float triangleCoords[]={ // 按逆时针方向顺序:
            0.0f,0.622008459f,0.0f,// top
            -0.5f,-0.311004243f,0.0f,// bottom left
            0.5f,-0.311004243f,0.0f// bottom right
    };
    // 设置颜色，分别为red, green, blue 和alpha (opacity)
    private float color[]={0.63671875f,0.76953125f,0.22265625f,1.0f};
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
            "uniform mat4 uMVPMatrix;"+
                    "void main() {" +
                    "  gl_Position = uMVPMatrix*vPosition;" +
                    "}";
    /*
    一个标准的顶点着色器
    uniform mat4 uMVPMatrix;                             // 应用程序传入顶点着色器的总变换矩阵
    attribute vec4 aPosition;                            // 应用程序传入顶点着色器的顶点位置
    attribute vec2 aTextureCoord;                        // 应用程序传入顶点着色器的顶点纹理坐标
    attribute vec4 aColor                                // 应用程序传入顶点着色器的顶点颜色变量
    varying vec4 vColor                                  // 用于传递给片元着色器的顶点颜色数据
    varying vec2 vTextureCoord;                          // 用于传递给片元着色器的顶点纹理数据
    void main()
    {
        gl_Position = uMVPMatrix * aPosition;            // 根据总变换矩阵计算此次绘制此顶点位置
        vColor = aColor;                                 // 将顶点颜色数据传入片元着色器
        vTextureCoord = aTextureCoord;                   // 将接收的纹理坐标传递给片元着色器
    }
     */
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    /*
    一个标准的片元着色器
    precision mediump float;                           // 设置工作精度
        varying vec4 vColor;                               // 接收从顶点着色器过来的顶点颜色数据
        varying vec2 vTextureCoord;                        // 接收从顶点着色器过来的纹理坐标
        uniform sampler2D sTexture;                        // 纹理采样器，代表一幅纹理
        void main()
        {
            gl_FragColor = texture2D(sTexture, vTextureCoord) * vColor;// 进行纹理采样
        }
     */
    private int mProgram = -1;

    public Triangle(){
        // 为存放形状的坐标，初始化顶点字节缓冲
        ByteBuffer bb =ByteBuffer.allocateDirect(
        // (坐标数 * 4)float占四字节
            triangleCoords.length *4);
        // 设用设备的本点字节序
        bb.order(ByteOrder.nativeOrder());
        // 从ByteBuffer创建一个浮点缓冲
        vertexBuffer = bb.asFloatBuffer();
        // 把坐标们加入FloatBuffer中
        vertexBuffer.put(triangleCoords);
        // 设置buffer，从第一个坐标开始读
        vertexBuffer.position(0);

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
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

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
