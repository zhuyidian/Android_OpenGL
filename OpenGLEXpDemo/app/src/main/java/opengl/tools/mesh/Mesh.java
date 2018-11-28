package opengl.tools.mesh;


import android.graphics.Bitmap;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;


/**
 * 定义一个基类 Mesh,所有空间形体最基本的构成元素为Mesh（三角形网格）
 *
 * setVertices 允许子类重新定义顶点坐标。
 * setIndices 允许子类重新定义顶点的顺序。
 * setColor /setColors允许子类重新定义颜色。
 * x,y,z 定义了平移变换的参数。
 * rx,ry,rz 定义旋转变换的参数。
 *
 */

public class Mesh {
    // Our vertex buffer.
    private FloatBuffer verticesBuffer = null;
    // Our index buffer.
    private ShortBuffer indicesBuffer = null;
    // Our UV texture buffer.
    private FloatBuffer mTextureBuffer; // New variable.
    // Our texture id.
    private int mTextureId = -1; // New variable.
    // The bitmap we want to load as a texture.
    private Bitmap mBitmap; // New variable.
    // Indicates if we need to load the texture.
    private boolean mShouldLoadTexture = false; // New variable.
    // The number of indices.
    private int numOfIndices = -1;
    // Flat Color
    private float[] rgba = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
    // Smooth Colors
    private FloatBuffer colorBuffer = null;
    // Translate params.
    public float x = 0;
    public float y = 0;
    public float z = 0;
    // Rotate params.
    public float rx = 0;
    public float ry = 0;
    public float rz = 0;

    public void draw(GL10 gl) {
        // Counter-clockwise winding. 逆时针绕组
        gl.glFrontFace(GL10.GL_CCW);
        // Enable face culling. 启用面部剔除
        gl.glEnable(GL10.GL_CULL_FACE);
        // What faces to remove with the face culling.  面部剔除时要去除的面孔
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
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer);
        // Set flat color 设置平面颜色
        gl.glColor4f(rgba[0], rgba[1], rgba[2], rgba[3]);
        // Smooth color
        if (colorBuffer != null) {
            // Enable the color array buffer to be
            //used during rendering.
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
            gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
        }

        // 支持texture
        if (mShouldLoadTexture) {
            loadGLTexture(gl);
            mShouldLoadTexture = false;
        }
        if (mTextureId != -1 && mTextureBuffer != null) {
            gl.glEnable(GL10.GL_TEXTURE_2D);
            // Enable the texture state
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

            // Point to our buffers
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureId);
        }
        // end

        gl.glTranslatef(x, y, z);
        gl.glRotatef(rx, 1, 0, 0);
        gl.glRotatef(ry, 0, 1, 0);
        gl.glRotatef(rz, 0, 0, 1);

        // Point out the where the color buffer is.
        gl.glDrawElements(GL10.GL_TRIANGLES, numOfIndices, GL10.GL_UNSIGNED_SHORT, indicesBuffer);
        // Disable the vertices buffer.
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        // 支持texture
        if (mTextureId != -1 && mTextureBuffer != null) {
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        }
        // end

        // Disable face culling.
        gl.glDisable(GL10.GL_CULL_FACE);
    }

    protected void setVertices(float[] vertices) {
        // a float is 4 bytes, therefore
        //we multiply the number if
        // vertices with 4.
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        verticesBuffer = vbb.asFloatBuffer();
        verticesBuffer.put(vertices);
        verticesBuffer.position(0);
    }
    protected void setIndices(short[] indices) {
        // short is 2 bytes, therefore we multiply
        //the number if
        // vertices with 2.
        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indicesBuffer = ibb.asShortBuffer();
        indicesBuffer.put(indices);
        indicesBuffer.position(0);
        numOfIndices = indices.length;
    }
    /**
     * Set the texture coordinates.
     *
     * @param textureCoords
     */
    protected void setTextureCoordinates(float[] textureCoords) { // New
        // function.
        // float is 4 bytes, therefore we multiply the number if
        // vertices with 4.
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(textureCoords.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        mTextureBuffer = byteBuf.asFloatBuffer();
        mTextureBuffer.put(textureCoords);
        mTextureBuffer.position(0);
    }
    protected void setColor(float red, float green, float blue, float alpha) {
        // Setting the flat color.
        rgba[0] = red;
        rgba[1] = green;
        rgba[2] = blue;
        rgba[3] = alpha;
    }
    protected void setColors(float[] colors) {
        // float has 4 bytes.
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        colorBuffer = cbb.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);
    }
    /**
     * Set the bitmap to load into a texture.
     *
     * @param bitmap
     */
    public void loadBitmap(Bitmap bitmap) { // New function.
        this.mBitmap = bitmap;
        mShouldLoadTexture = true;
    }

    /**
     * Loads the texture.
     *
     * @param gl
     */
    private void loadGLTexture(GL10 gl) { // New function
        // 第一步：使用OpenGL库创建一个材质(Texture)，首先是获取一个Texture Id
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        mTextureId = textures[0];
        // 补充：textures中存放了创建的Texture ID，使用同样的Texture Id ，也可以来删除一个Texture： gl.glDeleteTextures(1, textures, 0)

        // 第二步：有了Texture Id之后，就可以通知OpenGL库使用这个Texture
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureId);

        // 第三步：需要给Texture填充设置参数，用来渲染的Texture可能比要渲染的区域大或者小，这是需要设置Texture需要放大或是缩小时OpenGL的模式
        // 常用的两种模式为GL10.GL_LINEAR(会得到一个较模糊的图像)和GL10.GL_NEAREST(需要比较清晰的图像使用)
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        // 第四步：设置绘制方式
        // Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
		/*
		有两种设置
			GL_REPEAT 重复Texture
			GL_CLAMP_TO_EDGE 只靠边线绘制一次
		可以试试四种组合：
		（1）
		GL_TEXTURE_WRAP_S（GL_REPEAT）
		GL_TEXTURE_WRAP_T（GL_REPEAT）
		（2）
		GL_TEXTURE_WRAP_S（GL_REPEAT）
		GL_TEXTURE_WRAP_T（GL_CLAMP_TO_EDGE）
		（3）
		GL_TEXTURE_WRAP_S（GL_CLAMP_TO_EDGE）
		GL_TEXTURE_WRAP_T（GL_REPEAT）
		（4）
		GL_TEXTURE_WRAP_S（GL_CLAMP_TO_EDGE）
		GL_TEXTURE_WRAP_T（GL_CLAMP_TO_EDGE）
		 */
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

        // 第五步：将Bitmap资源和Texture绑定起来
        // Use the Android GLUtils to specify a two-dimensional texture image
        // from our bitmap
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
    }
}