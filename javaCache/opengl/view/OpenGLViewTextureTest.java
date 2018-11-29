package opengl.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;

import opengl.tools.mesh.SimplePlane;

//视图类
public class OpenGLViewTextureTest extends GLSurfaceView {
	private OpenGLRendererTextureTest mRenderer;

	public OpenGLViewTextureTest(Context context) {
		super(context);
		// Create an OpenGL ES 2.0 context
		//setEGLContextClientVersion(2);
		mRenderer = new OpenGLRendererTextureTest();
		setRenderer(mRenderer);

		// Create a new plane.
		SimplePlane plane = new SimplePlane(1, 1);
		// Move and rotate the plane.
		plane.z = 1.7f;
		plane.rx = -65;
		// Load the texture.
		plane.loadBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.jay));
		// Add the plane to the renderer.
		mRenderer.addMesh(plane);
	}
}