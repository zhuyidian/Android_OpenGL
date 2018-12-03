package design.zhu.com.gl20.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import design.zhu.com.gl20.common.graphics.Triangle;
import design.zhu.com.gl20.common.graphics.Triangle3d;

public class OpenGLRendererTriangle3d implements GLSurfaceView.Renderer {
	private Triangle3d mTriangle3d;
	
	public OpenGLRendererTriangle3d() {

	}

	/**
	 * 渲染器
	 * 实现了下面三个方法 : 
	 * 		界面创建 : 
	 * 		界面改变 : 
	 * 		界面绘制 : 
	 * @author HanShuliang
	 *
	 */
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		//设置屏幕背景色
		GLES20.glClearColor(0, 0, 0, 1.0f);

		//创建三角形对象
		mTriangle3d = new Triangle3d();
		mTriangle3d.onSurfaceCreated(gl, config);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		//设置视窗大小及位置
		GLES20.glViewport(0, 0, width, height);

		mTriangle3d.onSurfaceChanged(gl,width,height);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		//清除深度缓冲与颜色缓冲
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		//绘制三角形
		mTriangle3d.onDrawFrame(gl);
	}
	
	

}
