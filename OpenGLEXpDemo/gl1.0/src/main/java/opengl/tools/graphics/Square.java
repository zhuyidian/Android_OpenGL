package opengl.tools.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import opengl.tools.CoordinateTransformation;

/**
 * Created by aserbao on 2018 2018/1/18.22:32
 * Email:aserbao@163.com
 * weixin: aserbao
 */

public class Square {
    // Our vertices.
    private float vertices[] = {
//            -1.0f,  1.0f, 0.0f,  // 0, Top Left
//            -1.0f, -1.0f, 0.0f,  // 1, Bottom Left
//            1.0f, -1.0f, 0.0f,  // 2, Bottom Right
//            1.0f,  1.0f, 0.0f,  // 3, Top Right
            CoordinateTransformation.toGLX(20.0f),CoordinateTransformation.toGLY(20.0f),0.0f,   // 0, Top Left
            CoordinateTransformation.toGLX(20.0f),CoordinateTransformation.toGLY(200.0f),0.0f,   // 1, Bottom Left
            CoordinateTransformation.toGLX(200.0f),CoordinateTransformation.toGLY(200.0f),0.0f,  // 2, Bottom Right
            CoordinateTransformation.toGLX(200.0f),CoordinateTransformation.toGLY(20.0f),0.0f,  // 3, Top Right
    };
    // The order we like to connect them. 按照顶点顺序连接
    private short[] indices = { 0, 1, 2, 0, 2, 3 };
    // Our vertex buffer.
    private FloatBuffer vertexBuffer;
    // Our index buffer.
    private ShortBuffer indexBuffer;

    public Square() {
        // a float is 4 bytes, therefore we
        // multiply the number if
        // vertices with 4.
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // short is 2 bytes, therefore we multiply
        //the number if
        // vertices with 2.
        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);
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
        // 连接顶点
        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);

        // Disable the vertices buffer. 禁用顶点缓冲区 不再需要时，关闭顶点数组
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        // Disable face culling. 禁用面部剔除
        gl.glDisable(GL10.GL_CULL_FACE);
    }
}
