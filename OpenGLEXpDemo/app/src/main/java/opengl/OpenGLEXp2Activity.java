package opengl;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import opengl.tools.DataManage;
import opengl.view.OpenGLViewGrid;
import opengl.view.OpenGLViewTriangle;
import opengl.view.Tree3DSurfaceView;

public class OpenGLEXp2Activity extends Activity {
	private OpenGLViewGrid mOpenGLViewGrid;
    private OpenGLViewTriangle mOpenGLViewTriangle;
    private Tree3DSurfaceView mTree3DSurfaceView;
    public static float WIDTH;
    public static float HEIGHT;
	private float w;
	private float h;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ȥ������
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //����ȫ��
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DataManage.init(this.getResources());

        //����
//        mOpenGLView = new OpenGLViewGrid(this);
//        DisplayMetrics md = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(md);
//        w = (float)(md.widthPixels);
//        h = (float)(md.heightPixels);
//        mOpenGLView.setWH(w, h);

        //����
//        mOpenGLViewTriangle = new OpenGLViewTriangle(this);

        //3D tree
        //���ϵͳ�Ŀ���Լ��߶�
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (dm.widthPixels > dm.heightPixels) {
            WIDTH = dm.widthPixels;
            HEIGHT = dm.heightPixels;
        } else {
            WIDTH = dm.heightPixels;
            HEIGHT = dm.widthPixels;
        }
        //����Ϊ����ģʽ
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mTree3DSurfaceView = new Tree3DSurfaceView(this);
        mTree3DSurfaceView.requestFocus();//��ȡ����
        mTree3DSurfaceView.setFocusableInTouchMode(true);//����Ϊ�ɴ���

        setContentView(mTree3DSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mTree3DSurfaceView!=null) {
            mTree3DSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mTree3DSurfaceView!=null) {
            mTree3DSurfaceView.onPause();
        }
    }
}