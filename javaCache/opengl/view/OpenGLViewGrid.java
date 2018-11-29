package opengl.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

//视图类
public class OpenGLViewGrid extends GLSurfaceView {
	private OpenGLRendererGrid mRenderer;
	private float width;
	private float height;
	//判断第一次
	public boolean FirstBool = true;

	public OpenGLViewGrid(Context context) {
		super(context);
		// Create an OpenGL ES 2.0 context
		//setEGLContextClientVersion(2);
		mRenderer = new OpenGLRendererGrid();
		setRenderer(mRenderer);
	}
	public void setWH(float w,float h) {
		width = w;
		height = h;
	}
	@Override
	///触摸屏事件，当在触摸屏上有动作时发生
	public boolean onTouchEvent(final MotionEvent event) {
//		queueEvent(new Runnable() {
//			@Override
//			public void run() {
//				mRenderer.setColor(event.getX()/getWidth(), event.getY()/getHeight(), 1.0f, event.getY()/getHeight());
//				if (FirstBool) {
//					FirstBool = false;
//					mRenderer.setFrist(FirstBool);
//					//mRenderer.setAngle(2);
//				}
//			}
//		});

		final int action = event.getAction();    
		final float x = event.getX()/width;    
        final float y = event.getY()/height;   
        switch (action) {
	        case MotionEvent.ACTION_DOWN:
	        	Log.e("GLSurfaceView", "currentX=" + x);
	        	break;
	        case MotionEvent.ACTION_MOVE:
	        	Log.e("GLSurfaceView", "currentX=" + y);
	        	break;
        }
		return true;
	}
}