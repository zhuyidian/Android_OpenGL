package design.zhu.com.gl20.common.graphics;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import design.zhu.com.gl20.BaseApplication;
import design.zhu.com.gl20.common.tools.ShaderUtil;

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

    private int mProgram = -1;
    /*
	顶点着色器和片元着色器
	 */
    private String vertexShaderCode;								//顶点着色器脚本代码
    private String fragmentShaderCode;							//片元着色器脚本代码
//    private final String vertexShaderCode =
//            "attribute vec4 vPosition;" +
//                    "uniform mat4 uMVPMatrix;"+
//                    "void main() {" +
//                    "  gl_Position = uMVPMatrix*vPosition;" +
//                    "}";
//    private final String fragmentShaderCode =
//            "precision mediump float;" +
//                    "uniform vec4 vColor;" +
//                    "void main() {" +
//                    "  gl_FragColor = vColor;" +
//                    "}";
    private float[] mMVPMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];

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
    }

    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig){
        vertexShaderCode = ShaderUtil.loadFromAssetsFile("squarevertex.sh", BaseApplication.getContext().getResources());
        fragmentShaderCode = ShaderUtil.loadFromAssetsFile("squarefrag.sh", BaseApplication.getContext().getResources());
        mProgram = ShaderUtil.createProgram(vertexShaderCode,fragmentShaderCode);
    }

    public void onSurfaceChanged(GL10 gl10, int width, int height){
        //得到投影矩阵
        float ratio =(float) width / height;
        // 此投影矩阵在onDrawFrame()中将应用到对象的坐标
        Matrix.frustumM(mProjectionMatrix,0,-ratio, ratio,-1,1,3,7);
        // 设置相机的位置(视口矩阵)
        Matrix.setLookAtM(mViewMatrix,0,0,0,-3,0f,0f,0f,0f,1.0f,0.0f);
        // 计算投影和视口变换
        Matrix.multiplyMM(mMVPMatrix,0, mProjectionMatrix,0, mViewMatrix,0);
    }

    public void onDrawFrame(GL10 gl10) {
        // 将program加入OpenGL ES环境中
        // 告诉OpenGL，使用我们准备好了的shader program来渲染
        GLES20.glUseProgram(mProgram);

        // 获得形状的变换矩阵的handle
        int mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram,"uMVPMatrix");
        // 应用投影和视口变换
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle,1,false, mMVPMatrix,0);

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
}
