package opengl.tools.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class ColorTriangle {
    private float[] mTriangleArray = {
            0f,1f,0f,
            -1f,-1f,0f,
            1f,-1f,0f
    };
    private FloatBuffer mTriangleBuffer;
    private float[] mColorArray={
            1f,0f,0f,1f,     //红
            0f,1f,0f,1f,     //绿
            0f,0f,1f,1f      //蓝
    };
    private FloatBuffer mColorBuffer;

    public ColorTriangle() {
        //先初始化buffer，数组的长度*4，因为一个float占4个字节
        ByteBuffer mbb = ByteBuffer.allocateDirect(mTriangleArray.length*4);
        //数组排序用nativeOrder
        mbb.order(ByteOrder.nativeOrder());
        mTriangleBuffer = mbb.asFloatBuffer();
        mTriangleBuffer.put(mTriangleArray);
        mTriangleBuffer.position(0);

        //先初始化buffer，数组的长度*4，因为一个float占4个字节
        ByteBuffer mcc = ByteBuffer.allocateDirect(mColorArray.length*4);
        //数组排序用nativeOrder
        mcc.order(ByteOrder.nativeOrder());
        mColorBuffer = mcc.asFloatBuffer();
        mColorBuffer.put(mColorArray);
        mColorBuffer.position(0);
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

        //使用数组作为颜色
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
        /* 说明：Specifies the location and data format of 指定的位置和数据格式
                 an array of vertex 一个顶点数组
                 coordinates to use when rendering. 渲染时使用的坐标
           函数使用：void glVertexPointer(int var1, int var2, int var3, Buffer var4)
                 参数var1：指绑定数组后，每个图形的顶点会用到数组中多少个数据做为顶点的数据
                 参数var2：规定调用数组的数据时应该用什么类型。一般与定义的数组的类型是一至的
                 参数var3：规定每个顶点应该从数组中移动的字节。一般情况下写0系统会自动识别。识别方式为size*sizeof(数组定义时报类型)
                 参数var4：要绑定数组的地址
        */
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mTriangleBuffer);

        //默認方式
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
        // 连接顶点，指定連接頂點順序
        //gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);

        // Disable the vertices buffer. 禁用顶点缓冲区 不再需要时，关闭顶点数组
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        // Disable face culling. 禁用面部剔除
        //gl.glDisable(GL10.GL_CULL_FACE);
        gl.glFinish();
    }
}
