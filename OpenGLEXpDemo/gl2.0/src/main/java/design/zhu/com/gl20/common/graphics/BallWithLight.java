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

public class BallWithLight {
    private float step=10f;
    private FloatBuffer vertexBuffer;
    private int vSize;

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

    public BallWithLight(){
        float[] dataPos=createBallPos();
        ByteBuffer buffer=ByteBuffer.allocateDirect(dataPos.length*4);
        buffer.order(ByteOrder.nativeOrder());
        vertexBuffer=buffer.asFloatBuffer();
        vertexBuffer.put(dataPos);
        vertexBuffer.position(0);
        vSize=dataPos.length/3;
    }

    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig){
        vertexShaderCode = ShaderUtil.loadFromAssetsFile("ballwithlightvertex.sh", BaseApplication.getContext().getResources());
        fragmentShaderCode = ShaderUtil.loadFromAssetsFile("ballwithlightfrag.sh", BaseApplication.getContext().getResources());
        program = ShaderUtil.createProgram(vertexShaderCode,fragmentShaderCode);
    }

    public void onSurfaceChanged(GL10 gl10, int width, int height){
        //得到投影矩阵
        float ratio =(float) width / height;
        // 调用此方法计算产生透视投影矩阵
        MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 3, 20);
        // 调用此方法产生摄像机9参数位置矩阵
        MatrixState.setCamera(0.0f, 0.0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
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

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,vSize);
        GLES20.glDisableVertexAttribArray(aPositionLocation);
    }

    private float[] createBallPos(){
        //球以(0,0,0)为中心，以R为半径，则球上任意一点的坐标为
        // ( R * cos(a) * sin(b),y0 = R * sin(a),R * cos(a) * cos(b))
        // 其中，a为圆心到点的线段与xz平面的夹角，b为圆心到点的线段在xz平面的投影与z轴的夹角
        ArrayList<Float> data=new ArrayList<>();
        float r1,r2;
        float h1,h2;
        float sin,cos;
        for(float i=-90;i<90+step;i+=step){
            r1 = (float)Math.cos(i * Math.PI / 180.0);
            r2 = (float)Math.cos((i + step) * Math.PI / 180.0);
            h1 = (float)Math.sin(i * Math.PI / 180.0);
            h2 = (float)Math.sin((i + step) * Math.PI / 180.0);
            // 固定纬度, 360 度旋转遍历一条纬线
            float step2=step*2;
            for (float j = 0.0f; j <360.0f+step; j +=step2 ) {
                cos = (float) Math.cos(j * Math.PI / 180.0);
                sin = -(float) Math.sin(j * Math.PI / 180.0);

                data.add(r2 * cos);
                data.add(h2);
                data.add(r2 * sin);
                data.add(r1 * cos);
                data.add(h1);
                data.add(r1 * sin);
            }
        }
        float[] f=new float[data.size()];
        for(int i=0;i<f.length;i++){
            f[i]=data.get(i);
        }
        return f;
    }
}
