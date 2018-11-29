package opengl.tools.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class DoubleTriangle {
    private FloatBuffer vertexBuffer;
    private int index=0;
    private float mTriangleArray[] = {
            -0.8f, -0.4f * 1.732f, 0.0f,
            0.0f, -0.4f * 1.732f, 0.0f,
            -0.4f, 0.4f * 1.732f, 0.0f,

            0.0f, -0.0f * 1.732f, 0.0f,
            0.8f, -0.0f * 1.732f, 0.0f,
            0.4f, 0.4f * 1.732f, 0.0f,
    };

    public DoubleTriangle() {
        //先初始化buffer，数组的长度*4，因为一个float占4个字节
        ByteBuffer mbb = ByteBuffer.allocateDirect(mTriangleArray.length*4);
        //数组排序用nativeOrder
        mbb.order(ByteOrder.nativeOrder());
        vertexBuffer = mbb.asFloatBuffer();
        vertexBuffer.put(mTriangleArray);
        vertexBuffer.position(0);
    }

    /**
     * This function draws our square on screen.
     * @param gl
     */
    public void draw(GL10 gl) {
        // Counter-clockwise winding. 逆时针绕组
        gl.glFrontFace(GL10.GL_CCW);
        // Enable face culling. 启用面部剔除
        gl.glEnable(GL10.GL_CULL_FACE);
        // What faces to remove with the face culling.面部剔除时要去除的面孔
        gl.glCullFace(GL10.GL_BACK);

        // Enabled the vertices buffer for writing 启用顶点缓冲区进行写入
        // and to be used during 并在期间使用
        // rendering. 渲染
        // 指定需要启用顶点数组
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        /* 说明：Specifies the location and data format of 指定的位置和数据格式
                 an array of vertex 一个顶点数组
                 coordinates to use when rendering. 渲染时使用的坐标
           函数使用：void glVertexPointer(int var1, int var2, int var3, Buffer var4)
                 参数var1：指绑定数组后，每个图形的顶点会用到数组中多少个数据做为顶点的数据
                 参数var2：规定调用数组的数据时应该用什么类型。一般与定义的数组的类型是一至的
                 参数var3：规定每个顶点应该从数组中移动的字节。一般情况下写0系统会自动识别。识别方式为size*sizeof(数组定义时报类型)
                 参数var4：要绑定数组的地址
        */
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

        index++;
        index%=10;
        switch(index){
            case 0:
            case 1:
            case 2:
                gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
                gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 6);  //使用VetexBuffer 来绘制，顶点的顺序由vertexBuffer中的顺序指定
                break;
            case 3:
            case 4:
            case 5:
                gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
                gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 6);
                break;
            case 6:
            case 7:
            case 8:
            case 9:
                gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
                gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 6);
                break;
        }

        // Disable the vertices buffer. 禁用顶点缓冲区 不再需要时，关闭顶点数组
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        // Disable face culling. 禁用面部剔除
        gl.glDisable(GL10.GL_CULL_FACE);
    }
}
