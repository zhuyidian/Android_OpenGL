package design.zhu.com.gl20.common.graphics;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import design.zhu.com.gl20.BaseApplication;
import design.zhu.com.gl20.common.tools.ShaderUtil;
import design.zhu.com.gl20.renderer.OpenGLRendererTriangle3d;

public class Triangle3d {
	//自定义渲染管线着色程序id
	int mProgram;										//自定义渲染管线着色程序id
	/*
	顶点着色器和片元着色器
	 */
	String mVertexShader;								//顶点着色器脚本代码
	String mFragmentShader;							//片元着色器脚本代码
	/*
	 * 下面的三个变量是顶点着色器中定义的三个变量
	 * 其中的总变换矩阵属性 是 一致变量
	 * 顶点位置 和 颜色属性 是 属性变量
	 */
	int muMVPMatrixHandle;								//总变换矩阵的引用
	int maPositionHandle;								//顶点位置属性引用
	int maColorHandle;									//顶点颜色属性引用
	public float[] mMVPMatrix;					//总变换矩阵
	public float[] mProjMatrix = new float[16];	//投影矩阵
	public float[] mVMatrix = new float[16];		//相机视口矩阵

	/*
	 * 这两个缓冲获得方法
	 * ①创建ByteBuffer, 创建时赋予大小 设置顺序
	 * ②将ByteBuffer 转为FloatBuffer
	 * ③给FloatBuffer设置值, 设置起始位置
	 */
	FloatBuffer mVertexBuffer;							//顶点坐标数据缓冲
	FloatBuffer mColorBuffer;							//顶点着色数据缓冲
	/*
	 * 这个变换矩阵 在设置变换 , 位移 , 旋转的时候 将参数设置到这个矩阵中去
	 */
	float[] mMMatrix = new float[16];			//具体物体的3D变换矩阵, 包括旋转, 平移, 缩放
	int vCount = 0;									//顶点数量    初始化的时候得到
	float xAngle = 0;									//绕x轴旋转角度  在GLSurfaceView线程中不停的变化
	private RotateThread mRotateThread;		//该线程用来改变图形角度
	private final float ANGLE_SPAN = 0.375f;		//三角形每次旋转的角度

	/**
	 * 构造方法 显示3D画面的载体
	 */
	public Triangle3d(){
		initVertexData();
	}
	
	/**
	 * 初始化顶点数据
	 * 
	 * 该方法制定顶点坐标和颜色数据, 并将数据输入到缓冲区
	 * 
	 * 创建一个ByteBuffer缓冲区, 然后将ByteBuffer缓冲区转为FloatBuffer缓冲区
	 * a. 创建float数组, 将对应的顶点(颜色)数据放到数组中去;
	 * b. 创建ByteBuffer对象, 根据之前创建的float数组的字节大小创建这个ByteBuffer对象,使用allocateDirect(int)分配大小
	 * c. 设置ByteBuffer对象的顺序, 调用order(ByteOrder.nativeOrder),设置为本地操作系统顺序
	 * d. 将ByteBuffer对象转为FloatBuffer对象, 调用asFloatBuffer()方法;
	 * e. 给FloatBuffer对象设置数组, 将开始创建的float数组设置给FloatBuffer对象;
	 * f. 设置FloatBuffer对象缓冲区的起始位置为0
	 */
	private void initVertexData() {
		//设置定点数为3
		vCount = 3;
		//计算三角形顶点的单位
		final float UNIT_SIZE = 0.2f;
		/*
		 * 这个float数组9个浮点数, 每3个为一个顶点的坐标
		 */
		float vertices[] = new float[]{
				-4 * UNIT_SIZE, 0 , 0,	//x轴左边的坐标
				0, -4 * UNIT_SIZE, 0,	//y轴坐标
				4 * UNIT_SIZE, 0, 0		//x轴右边的坐标
		};
		/*
		 * 创建一个ByteBuffer对象, 这个对象中缓冲区大小为vertices数组大小的4倍
		 * 因为每个float占4个字节, 创建的缓冲区大小正好将vertices装进去
		 */
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		//设置字节顺序为本地操作系统顺序
		vbb.order(ByteOrder.nativeOrder());
		//将该缓冲区转换为浮点型缓冲区
		mVertexBuffer = vbb.asFloatBuffer();
		//将顶点的位置数据写入到顶点缓冲区数组中
		mVertexBuffer.put(vertices);
		//设置缓冲区的起始位置为0
		mVertexBuffer.position(0);

		/*
		 * 顶点颜色数组
		 * 每四个浮点值代表一种颜色
		 */
		float colors[] = new float[]{
				1, 1, 1, 0,
				0, 0, 1, 0,
				0, 1, 0, 0
		};
		ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);//创建ByteBuffer
		cbb.order(ByteOrder.nativeOrder());//设置字节顺序
		mColorBuffer = cbb.asFloatBuffer();//将字节缓冲转为浮点缓冲
		mColorBuffer.put(colors);
		mColorBuffer.position(0);
	}
	
	/**
	 * 初始化着色器
	 * 
	 * 流程 : 
	 * 		① 从资源中获取顶点 和 片元着色器脚本
	 * 		② 根据获取的顶点 片元着色器脚本创建着色程序
	 * 		③ 从着色程序中获取顶点位置引用 , 顶点颜色引用,  总变换矩阵引用
	 * 
	 * @param mv MyTDView对象, 是GLSurfaceView对象
	 */
	public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig){
		mRotateThread = new RotateThread();
		mRotateThread.start();
		//打开深度检测
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		/*
		 * mVertextShader是顶点着色器脚本代码
		 * 调用工具类方法获取着色器脚本代码, 着色器脚本代码放在assets目录中
		 * 传入的两个参数是 脚本名称 和 应用的资源
		 * 应用资源Resources就是res目录下的那写文件
		 */
		mVertexShader = ShaderUtil.loadFromAssetsFile("triangle3dvertex.sh", BaseApplication.getContext().getResources());
		mFragmentShader = ShaderUtil.loadFromAssetsFile("triangle3dfrag.sh", BaseApplication.getContext().getResources());
		/*
		 * 创建着色器程序, 传入顶点着色器脚本 和 片元着色器脚本 注意顺序不要错
		 */
		mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
		/*
		 * 从着色程序中获取 属性变量 顶点坐标(颜色)数据的引用
		 * 其中的"aPosition"是顶点着色器中的顶点位置信息
		 * 其中的"aColor"是顶点着色器的颜色信息
		 */
		maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		maColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
		/*
		 * 从着色程序中获取一致变量  总变换矩阵
		 * uMVPMatrix 是顶点着色器中定义的一致变量
		 */
		muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
	}

	public void onSurfaceChanged(GL10 gl10, int width, int height){
		//计算GLSurfaceView的宽高比
		float ratio = (float)width/height;
		/*
		 * 产生透视矩阵
		 * 参数介绍 :
		 * ① 4 * 4 投影矩阵
		 * ② 投影矩阵的起始位置
		 * 后面的四个参数分别是 左 右 下 上 的距离
		 * 最后两个参数是 近视点 和 远视点 距离
		 */
		Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
		/*
		 * 设置摄像机参数矩阵
		 * 参数介绍 :
		 * 前两个参数是摄像机参数矩阵 和 矩阵数组的起始位置
		 * 后面三个一组是三个空间坐标 先后依次是 摄像机的位置  看的方向 摄像机上方朝向
		 */
		Matrix.setLookAtM(mVMatrix, 0, 0f,0f,3f,0f,0f,0f,0f,1.0f,0.0f);
	}
	
	/**
	 * 绘制三角形方法
	 * 
	 * 绘制流程 : 
	 * 		① 指定着色程序
	 * 		② 设置变换矩阵
	 * 		③ 将顶点位置 颜色 数据传进渲染管线
	 * 		④ 启动顶点位置 颜色 数据
	 * 		⑤ 执行绘制
	 */
	public void onDrawFrame(GL10 gl10) {
		//根据着色程序id 指定要使用的着色器
		GLES20.glUseProgram(mProgram);
		/*
		 * 设置旋转变化矩阵 
		 * 参数介绍 : ① 3D变换矩阵 ② 矩阵数组的起始索引 ③旋转的角度 ④⑤⑥
		 */
		Matrix.setRotateM(mMMatrix, 0, 0, 0, 1, 0);
		/*
		 * 设置沿z轴正方向位移
		 * 参数介绍 : ① 变换矩阵 ② 矩阵索引开始位置 ③④⑤设置位移方向z轴
		 */
		Matrix.translateM(mMMatrix, 0, 0, 0, 1);
		/*
		 * 设置绕x轴旋转
		 * 参数介绍 : ① 变换矩阵 ② 索引开始位置 ③ 旋转角度 ④⑤⑥ 设置绕哪个轴旋转
		 */
		Matrix.rotateM(mMMatrix, 0, xAngle, 1, 0, 0);
		/*
		 * 应用投影和视口变换
		 */
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, getFianlMatrix(mMMatrix), 0);
		/*
		 * 将顶点位置数据传送进渲染管线, 为画笔指定定点的位置数据
		 */
		GLES20.glVertexAttribPointer(
				maPositionHandle, 
				3, 
				GLES20.GL_FLOAT,
				false, 
				3 * 4, 
				mVertexBuffer
		);
		/*
		 * 将顶点颜色数据传送进渲染管线, 为画笔指定定点的颜色数据
		 */
		GLES20.glVertexAttribPointer(
				maColorHandle, 
				4, 
				GLES20.GL_FLOAT,
				false, 
				4 * 4, 
				mColorBuffer
		);
		//启用顶点位置数据
		GLES20.glEnableVertexAttribArray(maPositionHandle);
		//启用顶点颜色数据
		GLES20.glEnableVertexAttribArray(maColorHandle);
		//执行绘制
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
	}
	
	/**
	 * 计算最终投影的矩阵
	 * @param spec
	 * @return
	 */
	private float[] getFianlMatrix(float[] spec){
		mMVPMatrix = new float[16];
		/*
		 * 计算矩阵变换投影
		 * 
		 * 参数介绍 : 
		 * 	① 总变换矩阵				 ② 总变换矩阵起始索引
		 * 	③ 摄像机位置朝向矩阵		 ④ 摄像机朝向矩阵起始索引
		 * 	⑤ 投影变换矩阵 			 ⑥ 投影变换矩阵起始索引
		 */
		Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, spec, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
		return mMVPMatrix;
	}
	/**
	 * 这个线程是用来改变三角形角度用的
	 */
	public class RotateThread extends Thread {
		public boolean flag = true;

		@Override
		public void run() {
			while(flag){
				xAngle = xAngle + ANGLE_SPAN;
				try {
					Thread.sleep(20);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
