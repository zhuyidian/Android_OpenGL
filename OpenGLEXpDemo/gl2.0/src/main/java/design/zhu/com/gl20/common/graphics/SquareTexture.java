package design.zhu.com.gl20.common.graphics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import design.zhu.com.gl20.BaseApplication;
import design.zhu.com.gl20.common.tools.MatrixState;
import design.zhu.com.gl20.common.tools.ShaderUtil;

public class SquareTexture {
    private FloatBuffer vertexBuffer;
    private FloatBuffer bCoord;
    private ShortBuffer indexBuffer;
    // 每个顶点的坐标数
    private final int COORDS_PER_VERTEX =3;
    //根据纹理映射原理中的介绍，我们将顶点坐标设置为：
    private final float[] triangleCoords={
            -1.0f,1.0f,    //左上角
            -1.0f,-1.0f,   //左下角
            1.0f,1.0f,     //右上角
            1.0f,-1.0f     //右下角
    };
    //相应的，对照顶点坐标，我们可以设置纹理坐标为：
    private final float[] sCoord={
            0.0f,0.0f,
            0.0f,1.0f,
            1.0f,0.0f,
            1.0f,1.0f,
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
    private Bitmap mBitmap;

    private int mProgram = -1;
    /*
	顶点着色器和片元着色器
	 */
    private String vertexShaderCode;								//顶点着色器脚本代码
    private String fragmentShaderCode;							//片元着色器脚本代码
    private float[] mMVPMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    int mMVPMatrixHandle;
    int mPositionHandle;
    int glHCoordinate;
    int glHTexture;
    private int textureId;

    public SquareTexture(){
        ByteBuffer bb = ByteBuffer.allocateDirect(
                triangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);

        ByteBuffer cc=ByteBuffer.allocateDirect(sCoord.length*4);
        cc.order(ByteOrder.nativeOrder());
        bCoord=cc.asFloatBuffer();
        bCoord.put(sCoord);
        bCoord.position(0);

        try {
            mBitmap = BitmapFactory.decodeStream(BaseApplication.getContext().getResources().getAssets().open("fengj.png"));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig){
        vertexShaderCode = ShaderUtil.loadFromAssetsFile("squaretexturevertex.sh", BaseApplication.getContext().getResources());
        fragmentShaderCode = ShaderUtil.loadFromAssetsFile("squaretexturefrag.sh", BaseApplication.getContext().getResources());
        mProgram = ShaderUtil.createProgram(vertexShaderCode,fragmentShaderCode);

        // 获得形状的变换矩阵的handle
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram,"vMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(mProgram,"vPosition");
        glHCoordinate=GLES20.glGetAttribLocation(mProgram,"vCoordinate");
        glHTexture=GLES20.glGetUniformLocation(mProgram,"vTexture");
    }

    public void onSurfaceChanged(GL10 gl10, int width, int height){
        if(mBitmap==null) return;
        int w=mBitmap.getWidth();
        int h=mBitmap.getHeight();
        float sWH=w/(float)h;
        float sWidthHeight=width/(float)height;
        if(width>height){
            if(sWH>sWidthHeight){
                //Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight*sWH,sWidthHeight*sWH, -1,1, 3, 7);
                MatrixState.orthoM(-sWidthHeight*sWH,sWidthHeight*sWH, -1,1, 3, 7);
            }else{
                //Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight/sWH,sWidthHeight/sWH, -1,1, 3, 7);
                MatrixState.orthoM(-sWidthHeight/sWH,sWidthHeight/sWH, -1,1, 3, 7);
            }
        }else{
            if(sWH>sWidthHeight){
                //Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1/sWidthHeight*sWH, 1/sWidthHeight*sWH,3, 7);
                MatrixState.orthoM(-1, 1, -1/sWidthHeight*sWH, 1/sWidthHeight*sWH,3, 7);
            }else{
                //Matrix.orthoM(mProjectMatrix, 0, -1, 1, -sWH/sWidthHeight, sWH/sWidthHeight,3, 7);
                MatrixState.orthoM( -1, 1, -sWH/sWidthHeight, sWH/sWidthHeight,3, 7);
            }
        }
        // 此投影矩阵在onDrawFrame()中将应用到对象的坐标
        //MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 3, 7);
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

        // 应用投影和视口变换
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle,1,false, MatrixState.getFinalMatrix(),0);
        // 启用一个指向三角形的顶点数组的handle
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(glHCoordinate);
        GLES20.glUniform1i(glHTexture, 0);
        textureId=createTexture();
        GLES20.glVertexAttribPointer(mPositionHandle,2,GLES20.GL_FLOAT,false,0,vertexBuffer);
        GLES20.glVertexAttribPointer(glHCoordinate,2,GLES20.GL_FLOAT,false,0,bCoord);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
    }

    private int createTexture(){
        int[] texture=new int[1];
        if(mBitmap!=null&&!mBitmap.isRecycled()){
            //生成纹理
            GLES20.glGenTextures(1,texture,0);
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
            return texture[0];
        }
        return 0;
    }
}
