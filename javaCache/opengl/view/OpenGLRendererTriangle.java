package opengl.view;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import opengl.tools.Triangle;
import opengl.tools.TriangleColor;

/**
 ������GlSurfaceView.Renderer�еĻ��Ʋ��裺
 ��һ����������ͼչʾ����(viewport) :��onSurfaceChanged�е���GLES20.glViewport(0, 0, width, height);
 �ڶ���������ͼ���࣬ȷ���ö���λ�ú�ͼ����ɫ�����������ɫ����ת��ΪOpenGlʹ�õ����ݸ�ʽ
 �����������ض�����ɫ����Ƭ����ɫ�������޸�ͼ�ε���ɫ���������������
 ���Ĳ�������ͶӰ�������ͼ����ʾ��ͼ����ʾ״̬������ͶӰ�������ͼ��ת�����ݸ���ɫ����
 ���岽��������Ŀ(Program),���Ӷ�����ɫ��Ƭ����ɫ����
 �����������������ݴ��뵽OpenGl ES������
 */
//��Ⱦ��
public class OpenGLRendererTriangle implements Renderer {
	private Triangle mTriangle;
	private TriangleColor mTriangleColor;

	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		// Set the background frame color
		GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		// initialize a triangle
		mTriangle = new Triangle();
		mTriangleColor = new TriangleColor();
	}
	private float[] mRotationMatrix = new float[16];
	public volatile float mAngle;

	public float getAngle() {
		return mAngle;
	}

	public void setAngle(float angle) {
		mAngle = angle;
	}
	public void onDrawFrame(GL10 unused) {
		// �������λ�ã��鿴����
		Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

		// ����ͶӰ����ͼ�任
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

		float[] scratch = new float[16];
		// Ϊ�����δ���һ����ת�任
//        long time = SystemClock.uptimeMillis() % 4000L;
//        float angle = 9f * ((int) time);
		Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, -1.0f);

		// Combine the rotation matrix with the projection and camera view
		// Note that the mMVPMatrix factor *must be first* in order
		// for the matrix multiplication product to be correct.
		Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
		// ������״
//        mTriangle.draw(scratch);
		mTriangle.draw(scratch);
	}

	// mMVPMatrix is an abbreviation for "Model View Projection Matrix"
	private final float[] mMVPMatrix = new float[16];
	private final float[] mProjectionMatrix = new float[16];
	private final float[] mViewMatrix = new float[16];
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);

		float ratio = (float) width / height;

		// ���ͶӰ����Ӧ���ڶ���������onDrawFrame����������
		Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
	}

	public static int loadShader(int type, String shaderCode){

		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shader = GLES20.glCreateShader(type);


		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		return shader;
	}
}