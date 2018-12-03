package design.zhu.com.gl20.common.graphics;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import design.zhu.com.gl20.BaseApplication;
import design.zhu.com.gl20.common.tools.MatrixState;
import design.zhu.com.gl20.common.tools.ShaderUtil;

public class Cylinder {
    //顶点坐标
    private FloatBuffer vertexBuffer;
    private int n=360;  //切割份数
    private float height=2.0f;  //圆锥高度
    private float radius=1.0f;  //圆锥底面半径
    private float[] colors={1.0f,1.0f,1.0f,1.0f};
    private int vSize;
    private Oval ovalTop,ovalBootom;

    private int program;
    private final String A_POSITION = "vPosition";
    private final String U_MATRIX = "vMatrix";
    private int uMatrixLocation;
    private int aPositionLocation;
    /*
	顶点着色器和片元着色器
	 */
    private String vertexShaderCode;								//顶点着色器脚本代码
    private String fragmentShaderCode;							//片元着色器脚本代码

    public Cylinder(){
        ovalBootom=new Oval(0.0f);
        ovalTop=new Oval(height);
        ArrayList<Float> pos=new ArrayList<>();
        float angDegSpan=360f/n;
        for(float i=0;i<360+angDegSpan;i+=angDegSpan){
            pos.add((float) (radius*Math.sin(i*Math.PI/180f)));
            pos.add((float)(radius*Math.cos(i*Math.PI/180f)));
            pos.add(height);
            pos.add((float) (radius*Math.sin(i*Math.PI/180f)));
            pos.add((float)(radius*Math.cos(i*Math.PI/180f)));
            pos.add(0.0f);
        }
        float[] d=new float[pos.size()];
        for (int i=0;i<d.length;i++){
            d[i]=pos.get(i);
        }
        vSize=d.length/3;
        ByteBuffer buffer=ByteBuffer.allocateDirect(d.length*4);
        buffer.order(ByteOrder.nativeOrder());
        vertexBuffer=buffer.asFloatBuffer();
        vertexBuffer.put(d);
        vertexBuffer.position(0);
    }

    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig){
        vertexShaderCode = ShaderUtil.loadFromAssetsFile("conevertex.sh", BaseApplication.getContext().getResources());
        fragmentShaderCode = ShaderUtil.loadFromAssetsFile("conefrag.sh", BaseApplication.getContext().getResources());
        program = ShaderUtil.createProgram(vertexShaderCode,fragmentShaderCode);
    }

    public void onSurfaceChanged(GL10 gl10, int width, int height){
        //得到投影矩阵
        float ratio =(float) width / height;
        // 调用此方法计算产生透视投影矩阵
        MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 3, 20);
        // 调用此方法产生摄像机9参数位置矩阵
        MatrixState.setCamera(1.0f, -10.0f, -4.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }

    public void onDrawFrame(GL10 gl10) {
        // 将program加入OpenGL ES环境中
        // 告诉OpenGL，使用我们准备好了的shader program来渲染
        GLES20.glUseProgram(program);

        // 获得形状的变换矩阵的handle
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, MatrixState.getFinalMatrix(),0);

        // 获取指向vertex shader的成员vPosition的 handle
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        //---------传入顶点数据数据
        GLES20.glVertexAttribPointer(aPositionLocation, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(aPositionLocation);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,vSize);
        GLES20.glDisableVertexAttribArray(aPositionLocation);

        ovalBootom.setMatrix(MatrixState.getFinalMatrix());
        ovalBootom.onDrawFrame(gl10);
        ovalTop.setMatrix(MatrixState.getFinalMatrix());
        ovalTop.onDrawFrame(gl10);
    }
}
