package design.zhu.com.gl20.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import design.zhu.com.gl20.tools.Square;

public class OpenGLRendererSquare implements Renderer {
	private Square mSquare;
	private final float[] mMVPMatrix = new float[16];
	private final float[] mProjectionMatrix = new float[16];
	private final float[] mViewMatrix = new float[16];

	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
		// 参数顺序 r, g, b, a 画成黑色，不透明
		GLES20.glClearColor(0f, 0f, 0f, 1f);

		//your init
		// 初始化一个三角形
		mSquare = new Square();
	}

	@Override
	public void onSurfaceChanged(GL10 gl10, int i, int i1) {
		// 参数是left, top, width, height
		GLES20.glViewport(0, 0, i, i1);

		//得到投影矩阵
		float ratio =(float) i / i1;
		// 此投影矩阵在onDrawFrame()中将应用到对象的坐标
		Matrix.frustumM(mProjectionMatrix,0,-ratio, ratio,-1,1,3,7);
	}

	@Override
	public void onDrawFrame(GL10 gl10) {
		// 清除颜色缓冲区，因为我们要开始新一帧的绘制了，所以先清理，以免有脏数据
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

		// 设置相机的位置(视口矩阵)
		Matrix.setLookAtM(mViewMatrix,0,0,0,-3,0f,0f,0f,0f,1.0f,0.0f);
		// 计算投影和视口变换
		Matrix.multiplyMM(mMVPMatrix,0, mProjectionMatrix,0, mViewMatrix,0);
		mSquare.draw(mMVPMatrix);
	}
}