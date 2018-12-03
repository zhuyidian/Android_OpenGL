package design.zhu.com.gl20.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import design.zhu.com.gl20.common.graphics.Circle;

public class OpenGLRendererCircle implements Renderer {
	private Circle mCircle;

	public OpenGLRendererCircle(){
	}

	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
		// 参数顺序 r, g, b, a 画成黑色，不透明
		GLES20.glClearColor(0f, 0f, 0f, 1f);

		//your init
		// 初始化一个三角形
		mCircle = new Circle();
		mCircle.onSurfaceCreated(gl10, eglConfig);
	}

	@Override
	public void onSurfaceChanged(GL10 gl10, int i, int i1) {
		// 参数是left, top, width, height
		GLES20.glViewport(0, 0, i, i1);

		mCircle.onSurfaceChanged(gl10,i,i1);
	}

	@Override
	public void onDrawFrame(GL10 gl10) {
		// 清除颜色缓冲区，因为我们要开始新一帧的绘制了，所以先清理，以免有脏数据
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

		mCircle.onDrawFrame(gl10);
	}
}