package design.zhu.com.gl20;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import design.zhu.com.gl20.renderer.OpenGLRendererSquareTexture;

public class TestActivity extends AppCompatActivity {
    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new GLSurfaceView(this);
        // 我们要使用OpenGL ES 2.0，所以要setEGLContextClientVersion(2)
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(new OpenGLRendererSquareTexture());
        // 渲染模式(render mode)分为两种，一个是GLSurfaceView主动刷新(continuously)，不停的回调Renderer的onDrawFrame，
        // 另外一种叫做被动刷新（when dirty）(GLSurfaceView.RENDERMODE_WHEN_DIRTY)，就是当请求刷新时才调一次onDrawFrame
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        setContentView(mGLSurfaceView);
    }
}
