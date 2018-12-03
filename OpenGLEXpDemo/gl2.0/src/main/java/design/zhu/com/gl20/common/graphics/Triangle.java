package design.zhu.com.gl20.common.graphics;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import design.zhu.com.gl20.BaseApplication;
import design.zhu.com.gl20.common.tools.ShaderUtil;

public class Triangle {
    private FloatBuffer vertexBuffer;
    // 数组中每个顶点的坐标数
    private final int COORDS_PER_VERTEX =3;
    private float triangleCoords[] = {
            0.5f,  0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f  // bottom right
    };
    //设置颜色，依次为红绿蓝和透明通道
    float color[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    //顶点个数
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    //顶点之间的偏移量  每个顶点四个字节
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private int mProgram = -1;
    /*
	顶点着色器和片元着色器
	 */
    private String vertexShaderCode;								//顶点着色器脚本代码
    private String fragmentShaderCode;							//片元着色器脚本代码
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
    private final String A_POSITION = "vPosition";
    private final String U_COLOR = "vColor";
    private int uColorLocation;
    private int aPositionLocation;

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
    }

    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig){
        vertexShaderCode = ShaderUtil.loadFromAssetsFile("trianglevertex.sh", BaseApplication.getContext().getResources());
        fragmentShaderCode = ShaderUtil.loadFromAssetsFile("trianglefrag.sh", BaseApplication.getContext().getResources());
        mProgram = ShaderUtil.createProgram(vertexShaderCode,fragmentShaderCode);
        // 将program加入OpenGL ES环境中
        // 告诉OpenGL，使用我们准备好了的shader program来渲染
        GLES20.glUseProgram(mProgram);

        // 获取指向vertex shader的成员vPosition的 handle
        aPositionLocation = GLES20.glGetAttribLocation(mProgram, A_POSITION);
        // 获取指向fragment shader的成员vColor的handle
        uColorLocation = GLES20.glGetUniformLocation(mProgram, U_COLOR);
        //---------传入顶点数据数据
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(aPositionLocation, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        //---------传入颜色数据
        GLES20.glUniform4fv(uColorLocation, 1, color, 0);
    }

    public void onSurfaceChanged(GL10 gl10, int width, int height){

    }

    public void onDrawFrame(GL10 gl10) {
        // 画三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // 禁用指向三角形的顶点数组
        //GLES20.glDisableVertexAttribArray(aPositionLocation);
    }
}
