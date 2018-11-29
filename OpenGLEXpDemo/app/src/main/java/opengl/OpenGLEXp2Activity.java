package opengl;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import javax.microedition.khronos.opengles.GL10;

import opengl.interport.IOpenGLDemo;
import opengl.tools.CoordinateTransformation;
import opengl.tools.DataManage;
import opengl.tools.graphics.DrawIcosahedron;
import opengl.tools.graphics.DrawSphere;
import opengl.view.OpenGLRendererPublicTest;
import opengl.view.OpenGLViewGrid;
import opengl.view.OpenGLViewTest;
import opengl.view.OpenGLViewTextureTest;
import opengl.view.OpenGLViewTriangle;
import opengl.view.Tree3DSurfaceView;

public class OpenGLEXp2Activity extends Activity implements IOpenGLDemo {
	private OpenGLViewGrid mOpenGLViewGrid;
    private OpenGLViewTriangle mOpenGLViewTriangle;
    private Tree3DSurfaceView mTree3DSurfaceView;
    private OpenGLViewTest mOpenGLViewTest;
    private OpenGLViewTextureTest mOpenGLViewTextureTest;
    private GLSurfaceView mGLSurfaceView;
    public static float WIDTH;
    public static float HEIGHT;
	private float w;
	private float h;

    private enum ChoiceOpenglDemoEnum{
	    GRID_OPENGL,
        TRIANGLE_OPENGL,
        TREE3D_OPENGL,
        TEST_OPENGL,
        TEXTURE_TEST_OPENGL,
        PUBLIC_TEST_OPENGL
    }
    private ChoiceOpenglDemoEnum choiceOpenglDemoEnum = ChoiceOpenglDemoEnum.PUBLIC_TEST_OPENGL;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DataManage.init(this.getResources());

        if(choiceOpenglDemoEnum == ChoiceOpenglDemoEnum.GRID_OPENGL) {
            mOpenGLViewGrid = new OpenGLViewGrid(this);
            DisplayMetrics md = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(md);
            w = (float)(md.widthPixels);
            h = (float)(md.heightPixels);
            mOpenGLViewGrid.setWH(w, h);
            setContentView(mOpenGLViewGrid);
        } else if(choiceOpenglDemoEnum == ChoiceOpenglDemoEnum.TRIANGLE_OPENGL) {
            mOpenGLViewTriangle = new OpenGLViewTriangle(this);
            setContentView(mOpenGLViewTriangle);
        } else if(choiceOpenglDemoEnum == ChoiceOpenglDemoEnum.TREE3D_OPENGL) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            if (dm.widthPixels > dm.heightPixels) {
                WIDTH = dm.widthPixels;
                HEIGHT = dm.heightPixels;
            } else {
                WIDTH = dm.heightPixels;
                HEIGHT = dm.widthPixels;
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mTree3DSurfaceView = new Tree3DSurfaceView(this);
            mTree3DSurfaceView.requestFocus();
            mTree3DSurfaceView.setFocusableInTouchMode(true);
            setContentView(mTree3DSurfaceView);
        } else if(choiceOpenglDemoEnum == ChoiceOpenglDemoEnum.TEST_OPENGL) {
            DisplayMetrics md = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(md);
            int ww = md.widthPixels;
            int hh = md.heightPixels;
            CoordinateTransformation.setWH(ww, hh);
            mOpenGLViewTest = new OpenGLViewTest(this);
            setContentView(mOpenGLViewTest);
        } else if(choiceOpenglDemoEnum == ChoiceOpenglDemoEnum.TEXTURE_TEST_OPENGL) {
            mOpenGLViewTextureTest = new OpenGLViewTextureTest(this);
            setContentView(mOpenGLViewTextureTest);
        } else if(choiceOpenglDemoEnum == ChoiceOpenglDemoEnum.PUBLIC_TEST_OPENGL) {
            DrawIcosahedron.init();
            mGLSurfaceView = new GLSurfaceView(this);
            mGLSurfaceView.setRenderer(new OpenGLRendererPublicTest(this));
            setContentView(mGLSurfaceView);
        }
    }

    @Override
    public void DrawScene(GL10 gl) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        // Clears the screen and depth buffer.
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        //画点
        //DrawPoint.DrawScene(gl);
        //画线
        //DrawLine.DrawScene(gl);
        //画三角形
        //DrawTriangle.DrawScene(gl);
        //画20面体
        //DrawIcosahedron.DrawScene(gl);
        //画太阳系
        //DrawSolarSystem.DrawScene(gl);
        //画球
        DrawSphere.DrawScene(gl);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(choiceOpenglDemoEnum == ChoiceOpenglDemoEnum.TREE3D_OPENGL) {
            if (mTree3DSurfaceView != null) {
                mTree3DSurfaceView.onResume();
            }
        }else if(choiceOpenglDemoEnum == ChoiceOpenglDemoEnum.PUBLIC_TEST_OPENGL) {
            mGLSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(choiceOpenglDemoEnum == ChoiceOpenglDemoEnum.TREE3D_OPENGL) {
            if (mTree3DSurfaceView != null) {
                mTree3DSurfaceView.onPause();
            }
        }else if(choiceOpenglDemoEnum == ChoiceOpenglDemoEnum.PUBLIC_TEST_OPENGL) {
            mGLSurfaceView.onPause();
        }
    }
}