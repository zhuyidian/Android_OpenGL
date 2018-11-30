package design.zhu.com.gl20.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenGLRendererPoint implements Renderer {
	/*
	gl_Position是一个内置变量，用于指定顶点，它是一个点，三维空间的点，所以用一个四维向量来赋值。vec4是四维向量的类型，vec4()是它的构造方法。
	gl_PointSize是另外一个内置变量，用于指定点的大小。
	 */
	private String VERTEX_SHADER =
			"void main() {\n" +
					"gl_Position = vec4(0.0, 0.0, 0.0, 1.0);\n" +
					"gl_PointSize = 20.0;\n" +
					"}\n";
	/*
	gl_FragColor是fragment shader的内置变量，用于指定当前顶点的颜色，四个分量（r, g, b, a）。这里是想指定为红色，不透明。
	 */
	private String FRAGMENT_SHADER =
			"void main() {\n" +
					"gl_FragColor = vec4(1., 0., 0.0, 1.0);\n" +
					"}\n";
	private int mGLProgram = -1;

	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
		// 参数顺序 r, g, b, a 画成黑色，不透明
		GLES20.glClearColor(0f, 0f, 0f, 1f);

		// 编译和链接vertex shader程序,也即顶点着色器
		int vsh = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
		// 告诉OpenGL，这一坨字串里面是vertex shader的源码
		GLES20.glShaderSource(vsh, VERTEX_SHADER);
		// 编译vertex shader
		GLES20.glCompileShader(vsh);

		// 编译和链接fragment shader程序,也即片元着色器
		int fsh = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
		// 告诉OpenGL，这一坨字串里面是fragment shader的源码
		GLES20.glShaderSource(fsh, FRAGMENT_SHADER);
		// 编译fragment shader
		GLES20.glCompileShader(fsh);

		// 创建shader program句柄
		mGLProgram = GLES20.glCreateProgram();
		// 把vertex shader添加到program
		GLES20.glAttachShader(mGLProgram, vsh);
		// 把fragment shader添加到program
		GLES20.glAttachShader(mGLProgram, fsh);
		// 做链接，可以理解为把两种shader进行融合，做好投入使用的最后准备工作
		GLES20.glLinkProgram(mGLProgram);

		// 如果shader编译或者链接过程出错了怎么办呢？能不能提早发现呢？当然，有办法检查一下，就是用接下来的这几句：
		// 让OpenGL来验证一下我们的shader program，并获取验证的状态
		GLES20.glValidateProgram(mGLProgram);
		int[] status = new int[1];
		// 获取验证的状态 如果有语法错误，编译错误，或者状态出错，这一步是能够检查出来的。如果一切正常，则取出来的status[0]为0
		GLES20.glGetProgramiv(mGLProgram, GLES20.GL_VALIDATE_STATUS, status, 0);
	}

	@Override
	public void onSurfaceChanged(GL10 gl10, int i, int i1) {
		// 参数是left, top, width, height
		GLES20.glViewport(0, 0, i, i1);
	}

	@Override
	public void onDrawFrame(GL10 gl10) {
		// 清除颜色缓冲区，因为我们要开始新一帧的绘制了，所以先清理，以免有脏数据
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

		// 告诉OpenGL，使用我们在onSurfaceCreated里面准备好了的shader program来渲染
		GLES20.glUseProgram(mGLProgram);
		// 开始渲染，发送渲染点的指令， 第二个参数是offset，第三个参数是点的个数。目前只有一个点，所以是1
		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
	}
}