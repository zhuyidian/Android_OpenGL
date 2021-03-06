package design.zhu.com.gl20.renderer.baserenderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import design.zhu.com.gl20.common.graphics.Cone;

public class OpenGLRendererCone implements Renderer {
	private Cone mCone;

	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
		//设置屏幕背景色RGBA
		GLES20.glClearColor(0.5f,0.5f,0.5f, 1.0f);
		//开启深度测试
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        //打开背面剪裁
		//GLES20.glEnable(GLES20.GL_CULL_FACE);
		mCone = new Cone();
		mCone.onSurfaceCreated(gl10, eglConfig);
	}

	@Override
	public void onSurfaceChanged(GL10 gl10, int i, int i1) {
		// 参数是left, top, width, height
		GLES20.glViewport(0, 0, i, i1);

		mCone.onSurfaceChanged(gl10,i,i1);
	}

	@Override
	public void onDrawFrame(GL10 gl10) {
		//清除深度缓冲与颜色缓冲
		GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

		mCone.onDrawFrame(gl10);
	}
}