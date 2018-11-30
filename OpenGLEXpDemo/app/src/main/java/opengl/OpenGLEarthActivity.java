package opengl;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import opengl.interport.IOpenGLDemo;
import opengl.tools.graphics.Ball;
import opengl.tools.graphics.Sphere;
import opengl.view.OpenGLRendererEarth;
import opengl.view.R;

public class OpenGLEarthActivity extends Activity implements IOpenGLDemo {
    private GLSurfaceView mGLSurfaceView;
    private float eyeX = 0f;
    private float eyeY = 0f;
    private float eyeZ = 4f;
    private Sphere sphere = new Sphere();
    private Ball ball = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setRenderer(new OpenGLRendererEarth(this));
        setContentView(mGLSurfaceView);
        mGLSurfaceView.setOnTouchListener(onTouchListener);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }
    @Override
    public void initLight(GL10 gl) {
        gl.glEnable(GL10.GL_LIGHTING);  //开启光源总开关
        initWhiteLight(gl, GL10.GL_LIGHT0, 0.5f, 0.5f, 0.5f);
    }
    @Override
    public void initObject(GL10 gl) {
        // TODO Auto-generated method stub
        ball = new Ball(5, initTexture(gl, R.drawable.earth));
    }
    @Override
    public void DrawScene(GL10 gl) {
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        // Clears the screen and depth buffer.
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glPushMatrix();
        ball.drawSelf(gl);
        gl.glPopMatrix();
    }
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        float lastX, lastY;
        private int mode = 0;
        float oldDist = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mode = 1;
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    mode += 1;
                    oldDist = caluDist(event);
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    mode -= 1;
                    break;

                case MotionEvent.ACTION_UP:
                    mode = 0;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (mode >= 2) {
                        float newDist = caluDist(event);
                        if (Math.abs(newDist - oldDist) > 2f) {
                            zoom(newDist, oldDist);
                        }
                    } else {
                        float dx = event.getRawX() - lastX;
                        float dy = event.getRawY() - lastY;

                        float a = 180.0f / 320;
                        ball.mAngleX += dx * a;
                        ball.mAngleY += dy * a;
                    }
                    break;
            }

            lastX = (int) event.getRawX();
            lastY = (int) event.getRawY();
            return true;
        }
    };

    /**
     * @param gl
     * @param cap
     * @param posX
     * @param posY
     * @param posZ
     */
    public void initWhiteLight(GL10 gl, int cap, float posX, float posY, float posZ) {
        gl.glEnable(cap);  //开启cap号光源

        //设置GL_LIGHT0光源的RGBA颜色值
        float[] ambientParams = { 1.0f, 1.0f, 1.0f, 1.0f };
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, ambientParams, 0);
        float[] diffuseParams = { 1.0f, 1.0f, 1.0f, 1.0f };
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, diffuseParams, 0);
        float[] specularParams = { 1f, 1f, 1f, 1.0f };
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, specularParams, 0);

        //设置GL_LIGHT0光源的位置
        float[] positionParams = { posX, posY, posZ, 1 }; //设置成点光源
        gl.glLightfv(cap, GL10.GL_POSITION, positionParams, 0);
    }
    /**
     * @param gl
     * @param resourceId
     * @return
     */
    public int initTexture(GL10 gl, int resourceId) {
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        int currTextureId = textures[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, currTextureId);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

        InputStream is = this.getResources().openRawResource(resourceId);
        Bitmap bitmapTmp;
        try {
            bitmapTmp = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmapTmp, 0);
        bitmapTmp.recycle();
        return currTextureId;
    }
    /**
     * @param gl
     */
    private void initMaterial(GL10 gl) {

        // ??????????????
        float ambientMaterial[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, ambientMaterial, 0);

        // ????????????
        float diffuseMaterial[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, diffuseMaterial, 0);

        // ??????????
        float specularMaterial[] = { 1f, 1f, 1f, 1.0f };
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, specularMaterial, 0);
        gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 100.0f);
    }
    public void zoom(float newDist, float oldDist) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        float px = displayMetrics.widthPixels;
        float py = displayMetrics.heightPixels;

        ball.zoom += (newDist - oldDist) * (ball.maxZoom - ball.minZoom) / FloatMath.sqrt(px * px + py * py) / 4;

        if (ball.zoom > ball.maxZoom) {
            ball.zoom = ball.maxZoom;
        } else if (ball.zoom < ball.minZoom) {
            ball.zoom = ball.minZoom;
        }
    }
    public float caluDist(MotionEvent event) {
        float dx = event.getX(0) - event.getX(1);
        float dy = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(dx * dx + dy * dy);
    }
}