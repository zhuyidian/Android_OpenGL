package design.zhu.com.gl20.common.graphics;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import design.zhu.com.gl20.BaseApplication;
import design.zhu.com.gl20.common.tools.MatrixState;
import design.zhu.com.gl20.common.tools.ShaderUtil;

public class Square1 {
    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    // 每个顶点的坐标数
    private final int COORDS_PER_VERTEX =3;
    private float triangleCoords[] = {
            -0.5f,  0.5f, 0.0f, // top left
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f, // bottom right
            0.5f,  0.5f, 0.0f  // top right
    };
    private short index[]={
            0,1,2,0,2,3
    };
    //设置颜色，依次为红绿蓝和透明通道
    float color[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    //顶点个数
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    //顶点之间的偏移量
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 每个顶点四个字节

    private int mProgram = -1;
    /*
	顶点着色器和片元着色器
	 */
    private String vertexShaderCode;								//顶点着色器脚本代码
    private String fragmentShaderCode;							//片元着色器脚本代码
    private float[] mMVPMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];

    public Square1(){
        ByteBuffer bb = ByteBuffer.allocateDirect(
                triangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);

        ByteBuffer cc= ByteBuffer.allocateDirect(index.length*2);
        cc.order(ByteOrder.nativeOrder());
        indexBuffer=cc.asShortBuffer();
        indexBuffer.put(index);
        indexBuffer.position(0);
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
        //Matrix.frustumM(mProjectionMatrix,0,-ratio, ratio,-1,1,3,7);
        MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 3, 7);
        // 设置相机的位置(视口矩阵)
        //Matrix.setLookAtM(mViewMatrix,0,0,0,-3,0f,0f,0f,0f,1.0f,0.0f);
        MatrixState.setCamera(0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // 计算投影和视口变换
        //Matrix.multiplyMM(mMVPMatrix,0, mProjectionMatrix,0, mViewMatrix,0);
    }

    public void onDrawFrame(GL10 gl10) {
        // 将program加入OpenGL ES环境中
        // 告诉OpenGL，使用我们准备好了的shader program来渲染
        GLES20.glUseProgram(mProgram);

        // 获得形状的变换矩阵的handle
        int mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram,"uMVPMatrix");
        // 应用投影和视口变换
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle,1,false, MatrixState.getFinalMatrix(),0);

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

        //索引法绘制正方形
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,index.length, GLES20.GL_UNSIGNED_SHORT,indexBuffer);

        // 禁用指向三角形的顶点数组
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
