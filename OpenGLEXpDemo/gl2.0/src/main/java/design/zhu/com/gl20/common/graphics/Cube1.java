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

public class Cube1 {
    //顶点坐标
    private FloatBuffer vertexBuffer;
    //颜色坐标
    private FloatBuffer colorBuffer;
    //顶点连接顺序
    private ShortBuffer indexBuffer;
    final float cubePositions[] = {
            -1.0f,1.0f,1.0f,    //正面左上0
            -1.0f,-1.0f,1.0f,   //正面左下1
            1.0f,-1.0f,1.0f,    //正面右下2
            1.0f,1.0f,1.0f,     //正面右上3
            -1.0f,1.0f,-1.0f,    //反面左上4
            -1.0f,-1.0f,-1.0f,   //反面左下5
            1.0f,-1.0f,-1.0f,    //反面右下6
            1.0f,1.0f,-1.0f,     //反面右上7
    };
    final short index[]={
            6,7,4,6,4,5,    //后面
            6,3,7,6,2,3,    //右面
            6,5,1,6,1,2,    //下面
            0,3,2,0,2,1,    //正面
            0,1,5,0,5,4,    //左面
            0,7,3,0,4,7,    //上面
    };
    float color[] = {
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
    };
    //float类型的字节数
    private final int BYTES_PER_FLOAT = 4;
    //共有72个顶点坐标，每个面包含12个顶点坐标
    private final int POSITION_COMPONENT_COUNT = 12*6;
    // 数组中每个顶点的坐标数
    private final int COORDS_PER_VERTEX = 3;
    // 颜色数组中每个颜色的值数
    private static final int COORDS_PER_COLOR = 4;
    //顶点个数
    private final int vertexCount = cubePositions.length / COORDS_PER_VERTEX;
    //顶点之间的偏移量
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 每个顶点四个字节

    private int program;
    private final String A_POSITION = "a_Position";
    private final String U_COLOR = "a_Color";
    private final String U_MATRIX = "u_Matrix";
    private int uMatrixLocation;
    private int uColorLocation;
    private int aPositionLocation;
    /*
	顶点着色器和片元着色器
	 */
    private String vertexShaderCode;								//顶点着色器脚本代码
    private String fragmentShaderCode;							//片元着色器脚本代码

    public Cube1(){
        ByteBuffer bb = ByteBuffer.allocateDirect(
                cubePositions.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(cubePositions);
        vertexBuffer.position(0);

        ByteBuffer dd = ByteBuffer.allocateDirect(
                color.length * 4);
        dd.order(ByteOrder.nativeOrder());
        colorBuffer = dd.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);

        ByteBuffer cc= ByteBuffer.allocateDirect(index.length*2);
        cc.order(ByteOrder.nativeOrder());
        indexBuffer=cc.asShortBuffer();
        indexBuffer.put(index);
        indexBuffer.position(0);
    }

    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig){
        vertexShaderCode = ShaderUtil.loadFromAssetsFile("square3dvertex.sh", BaseApplication.getContext().getResources());
        fragmentShaderCode = ShaderUtil.loadFromAssetsFile("square3dfrag.sh", BaseApplication.getContext().getResources());
        program = ShaderUtil.createProgram(vertexShaderCode,fragmentShaderCode);
        // 将program加入OpenGL ES环境中
        // 告诉OpenGL，使用我们准备好了的shader program来渲染
        GLES20.glUseProgram(program);

        // 获得形状的变换矩阵的handle
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        // 获取指向vertex shader的成员vPosition的 handle
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        // 获取指向fragment shader的成员vColor的handle
        uColorLocation = GLES20.glGetAttribLocation(program, U_COLOR);
        //---------传入顶点数据数据
        GLES20.glVertexAttribPointer(aPositionLocation, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        //---------传入颜色数据
        GLES20.glVertexAttribPointer(uColorLocation, COORDS_PER_COLOR, GLES20.GL_FLOAT, false, 0, colorBuffer);
        GLES20.glEnableVertexAttribArray(uColorLocation);
    }

    public void onSurfaceChanged(GL10 gl10, int width, int height){
        //得到投影矩阵
        float ratio =(float) width / height;
        // 调用此方法计算产生透视投影矩阵
        MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 3, 20);
        // 调用此方法产生摄像机9参数位置矩阵
        MatrixState.setCamera(5.0f, 5.0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }

    public void onDrawFrame(GL10 gl10) {
        // 应用投影和视口变换
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, MatrixState.getFinalMatrix(),0);
        //索引法绘制正方体
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,index.length, GLES20.GL_UNSIGNED_SHORT,indexBuffer);
    }
}
