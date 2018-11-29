package opengl.view;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import opengl.tools.DataManage;
import opengl.tools.OpenGLLine;
import opengl.tools.OpenGLPoint;

/**
 具体在GlSurfaceView.Renderer中的绘制步骤：
 第一步：设置视图展示窗口(viewport) :在onSurfaceChanged中调用GLES20.glViewport(0, 0, width, height);
 第二步：创建图形类，确定好顶点位置和图形颜色，将顶点和颜色数据转换为OpenGl使用的数据格式
 第三步：加载顶点找色器和片段着色器用来修改图形的颜色，纹理，坐标等属性
 第四步：创建投影和相机视图来显示视图的显示状态，并将投影和相机视图的转换传递给着色器。
 第五步：创建项目(Program),连接顶点着色器片段着色器。
 第六步：将坐标数据传入到OpenGl ES程序中
 */
//渲染类
public class OpenGLRendererGrid implements Renderer {
	private boolean mFrist = true;
	//颜色红绿蓝透明度
	private float R, G ,B ,A;
	private int rows = 10;
	private int texture;
	private int clums = 10;
	private float Point[];
	static short indices[];
	private float texcood[];
	private float[] texcood2=
		{0.0f, 1.0f,
			1.0f, 1.0f,
			0.0f, 0.0f,
			1.0f, 0.0f,};
	//定义角度
	private float Angle = 0;

	public int getRows() {
		return rows;
	}
	public int getClums() {
		return clums;
	}
	private FloatBuffer vertexBuffer;
	private FloatBuffer texcoodBuffer;
	private ShortBuffer indexBuffer;
	private void Init() {
		int k = 0;
		int f=0;
		int h=0;
		Point = new float[(rows+1)*(clums+1)*3];
		indices = new short[rows*6*clums];
		texcood = new float[(rows+1)*(clums+1)*2];
		
		for (int i = 0; i < rows+1; i++)
		{
			for (int j = 0; j < clums+1; j++)
			{
				try
				{
					Point[k] = (float) ((2.0/(float)rows)*i - 1);
					k++;
					Point[k] = (float) ((2.0/clums)*(clums - j)-1);
					k++;
					Point[k] = 0;
					k++;	
					
				} 
				catch (Exception e)
				{
					//TODO: handle exception
				}
				
			}
		}
		for (int i = 0; i < rows+1; i++)
		{
			for (int j = 0; j < clums+1; j++)
			{
				texcood[h] = (float)((1.0/rows)*i);
				h++;
				texcood[h] = (float)((1.0/clums)*j);
				h++;
			}
		}
		
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < clums; j++)
			{
				try
				{
					indices[f] = (short)(i * (clums + 1) + j);
					f++;
					indices[f] = (short)((i + 1) * (clums + 1) + j);
					f++;
					indices[f] = (short)((i + 1) * (clums + 1) + j + 1);
					f++;
					indices[f] = (short)(i * (clums + 1) + j);
					f++;
					indices[f] = (short)(i * (clums + 1) + j + 1);
					f++;
					indices[f] = (short)((i + 1) * (clums + 1) + j + 1);
					f++;
				} 
				catch (Exception e)
				{
					// TODO: handle exception
				}
			}
		}
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(Point.length * 4);  
		vbb.order(ByteOrder.nativeOrder());  
		vertexBuffer = vbb.asFloatBuffer();  
		vertexBuffer.put(Point);  
		vertexBuffer.position(0);  

		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);  
		ibb.order(ByteOrder.nativeOrder());  
		indexBuffer = ibb.asShortBuffer();  
		indexBuffer.put(indices);  
		indexBuffer.position(0); 
		
		ByteBuffer kbb = ByteBuffer.allocateDirect(texcood.length * 4);  
		kbb.order(ByteOrder.nativeOrder());  
		texcoodBuffer = kbb.asFloatBuffer();  
		texcoodBuffer.put(texcood);  
		texcoodBuffer.position(0); 
	}
	public void setColor(float r, float g, float b, float a) {
		R = r;
		G = g;
		B = b;
		A = a;
	}
	//设置mFrist
	public void setFrist(boolean first) {
		mFrist = first;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		//清除屏幕和深度缓存
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);
		//重置当前的模式观察矩阵
		gl.glLoadIdentity();
		//移入屏幕6.0
		gl.glTranslatef(0f, 0f, 6.0f);
		//改变屏幕颜色
		//gl.glClearColor(R, G, B, A);
		gl.glClearColor(0f,0f,0f,0f);
		//(颜色为红色)
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		gl.glEnable(GL10.GL_TEXTURE_2D);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);  
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer); 
		
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texcoodBuffer);
		
		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, indices.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		OpenGLPoint mopGlPoint = new OpenGLPoint();
		mopGlPoint.draw(gl);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		OpenGLLine mOpenGLLine = new OpenGLLine();
		mOpenGLLine.draw(gl);
		
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		// 设置输出屏幕大小
	    gl.glViewport(0, 0, width, height);
	}
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		//启用smooth shading(阴影平滑)
		gl.glShadeModel(GL10.GL_SMOOTH);
		//设置清除屏幕时所用的颜色（0F-1F） Set the background frame color
		gl.glClearColor(0f, 0f, 0f, 0f);
		//下面三行为深度缓存
		gl.glClearDepthf(1.0f);
		//启用深度测试
		gl.glEnable(GL10.GL_DEPTH_TEST);
		//所做深度测试的类型
		gl.glDepthFunc(GL10.GL_LEQUAL);
		//透视修正
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		   
		//打开纹理
		gl.glEnable(GL10.GL_TEXTURE_2D);
		//绑定纹理
		IntBuffer intBuffer = IntBuffer.allocate(1);
		//设置纹理个数
		gl.glGenTextures(1, intBuffer);
		texture = intBuffer.get(); 
		//开始绑定
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);
		Bitmap mBitmap = DataManage.getBitmap();
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap,0);
		
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
				
		Init();
	}
}