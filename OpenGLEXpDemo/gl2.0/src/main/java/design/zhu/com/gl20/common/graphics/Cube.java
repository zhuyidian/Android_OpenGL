package design.zhu.com.gl20.common.graphics;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import design.zhu.com.gl20.BaseApplication;
import design.zhu.com.gl20.common.tools.MatrixState;
import design.zhu.com.gl20.common.tools.ShaderUtil;

public class Cube {
    //顶点坐标
    private FloatBuffer vertexBuffer;
    //颜色坐标
    private FloatBuffer colorBuffer;
    //float类型的字节数
    private final int BYTES_PER_FLOAT = 4;
    //共有72个顶点坐标，每个面包含12个顶点坐标
    private final int POSITION_COMPONENT_COUNT = 12*6;
    // 数组中每个顶点的坐标数
    private final int COORDS_PER_VERTEX = 3;
    // 颜色数组中每个颜色的值数
    private static final int COORDS_PER_COLOR = 4;
    private final String A_POSITION = "a_Position";
    private final String U_COLOR = "a_Color";
    private final String U_MATRIX = "u_Matrix";
    private int uMatrixLocation;
    private int uColorLocation;
    private int aPositionLocation;
    private int program;
    private float vertices[] = {
            //前面
            0,0,1.0f,
            1.0f,1.0f,1.0f,
            -1.0f,1.0f,1.0f,
            0,0,1.0f,
            -1.0f,1.0f,1.0f,
            -1.0f,-1.0f,1.0f,
            0,0,1.0f,
            -1.0f,-1.0f,1.0f,
            1.0f,-1.0f,1.0f,
            0,0,1.0f,
            1.0f,-1.0f,1.0f,
            1.0f,1.0f,1.0f,
            //后面
            0,0,-1.0f,
            1.0f,1.0f,-1.0f,
            1.0f,-1.0f,-1.0f,
            0,0,-1.0f,
            1.0f,-1.0f,-1.0f,
            -1.0f,-1.0f,-1.0f,
            0,0,-1.0f,
            -1.0f,-1.0f,-1.0f,
            -1.0f,1.0f,-1.0f,
            0,0,-1.0f,
            -1.0f,1.0f,-1.0f,
            1.0f,1.0f,-1.0f,
            //左面
            -1.0f,0,0,
            -1.0f,1.0f,1.0f,
            -1.0f,1.0f,-1.0f,
            -1.0f,0,0,
            -1.0f,1.0f,-1.0f,
            -1.0f,-1.0f,-1.0f,
            -1.0f,0,0,
            -1.0f,-1.0f,-1.0f,
            -1.0f,-1.0f,1.0f,
            -1.0f,0,0,
            -1.0f,-1.0f,1.0f,
            -1.0f,1.0f,1.0f,
            //右面
            1.0f,0,0,
            1.0f,1.0f,1.0f,
            1.0f,-1.0f,1.0f,
            1.0f,0,0,
            1.0f,-1.0f,1.0f,
            1.0f,-1.0f,-1.0f,
            1.0f,0,0,
            1.0f,-1.0f,-1.0f,
            1.0f,1.0f,-1.0f,
            1.0f,0,0,
            1.0f,1.0f,-1.0f,
            1.0f,1.0f,1.0f,
            //上面
            0,1.0f,0,
            1.0f,1.0f,1.0f,
            1.0f,1.0f,-1.0f,
            0,1.0f,0,
            1.0f,1.0f,-1.0f,
            -1.0f,1.0f,-1.0f,
            0,1.0f,0,
            -1.0f,1.0f,-1.0f,
            -1.0f,1.0f,1.0f,
            0,1.0f,0,
            -1.0f,1.0f,1.0f,
            1.0f,1.0f,1.0f,
            //下面
            0,-1.0f,0,
            1.0f,-1.0f,1.0f,
            -1.0f,-1.0f,1.0f,
            0,-1.0f,0,
            -1.0f,-1.0f,1.0f,
            -1.0f,-1.0f,-1.0f,
            0,-1.0f,0,
            -1.0f,-1.0f,-1.0f,
            1.0f,-1.0f,-1.0f,
            0,-1.0f,0,
            1.0f,-1.0f,-1.0f,
            1.0f,-1.0f,1.0f
    };
    //顶点颜色值数组，每个顶点4个色彩值RGBA
    static float colors[]=new float[]{
            //前面
            1,1,1,0,//中间为白色
            1,0,0,0,
            1,0,0,0,
            1,1,1,0,//中间为白色
            1,0,0,0,
            1,0,0,0,
            1,1,1,0,//中间为白色
            1,0,0,0,
            1,0,0,0,
            1,1,1,0,//中间为白色
            1,0,0,0,
            1,0,0,0,
            //后面
            1,1,1,0,//中间为白色
            0,0,1,0,
            0,0,1,0,
            1,1,1,0,//中间为白色
            0,0,1,0,
            0,0,1,0,
            1,1,1,0,//中间为白色
            0,0,1,0,
            0,0,1,0,
            1,1,1,0,//中间为白色
            0,0,1,0,
            0,0,1,0,
            //左面
            1,1,1,0,//中间为白色
            1,0,1,0,
            1,0,1,0,
            1,1,1,0,//中间为白色
            1,0,1,0,
            1,0,1,0,
            1,1,1,0,//中间为白色
            1,0,1,0,
            1,0,1,0,
            1,1,1,0,//中间为白色
            1,0,1,0,
            1,0,1,0,
            //右面
            1,1,1,0,//中间为白色
            1,1,0,0,
            1,1,0,0,
            1,1,1,0,//中间为白色
            1,1,0,0,
            1,1,0,0,
            1,1,1,0,//中间为白色
            1,1,0,0,
            1,1,0,0,
            1,1,1,0,//中间为白色
            1,1,0,0,
            1,1,0,0,
            //上面
            1,1,1,0,//中间为白色
            0,1,0,0,
            0,1,0,0,
            1,1,1,0,//中间为白色
            0,1,0,0,
            0,1,0,0,
            1,1,1,0,//中间为白色
            0,1,0,0,
            0,1,0,0,
            1,1,1,0,//中间为白色
            0,1,0,0,
            0,1,0,0,
            //下面
            1,1,1,0,//中间为白色
            0,1,1,0,
            0,1,1,0,
            1,1,1,0,//中间为白色
            0,1,1,0,
            0,1,1,0,
            1,1,1,0,//中间为白色
            0,1,1,0,
            0,1,1,0,
            1,1,1,0,//中间为白色
            0,1,1,0,
            0,1,1,0,
    };
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

    public Cube(){
        vertexBuffer = ByteBuffer
                .allocateDirect(vertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        // 把坐标们加入FloatBuffer中
        vertexBuffer.put(vertices);
        // 设置buffer，从第一个坐标开始读
        vertexBuffer.position(0);

        //颜色buffer
        colorBuffer = ByteBuffer
                .allocateDirect(colors.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);
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
        MatrixState.setProjectFrustum(-ratio,ratio, -1, 1, 20, 100);
        // 调用此方法产生摄像机9参数位置矩阵
        MatrixState.setCamera(-16f, 8f, 45, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }

    public void onDrawFrame(GL10 gl10) {
        // 应用投影和视口变换
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, MatrixState.getFinalMatrix(),0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, POSITION_COMPONENT_COUNT);
    }
}
