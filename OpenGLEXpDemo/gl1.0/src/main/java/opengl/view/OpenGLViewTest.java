package opengl.view;

import android.content.Context;
import android.opengl.GLSurfaceView;

//视图类
public class OpenGLViewTest extends GLSurfaceView {
	private OpenGLRendererTest mRenderer;

	public OpenGLViewTest(Context context) {
		super(context);
		// Create an OpenGL ES 2.0 context
		//setEGLContextClientVersion(2);
		mRenderer = new OpenGLRendererTest();
		setRenderer(mRenderer);
	}
}