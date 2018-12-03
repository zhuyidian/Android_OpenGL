/*
 *
 * SGLRender.java
 * 
 * Created by Wuwang on 2016/10/15
 */
package design.zhu.com.gl20.renderer.imagerenderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;


import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import design.zhu.com.gl20.BaseApplication;

/**
 * Description:
 */
public class SGLRender implements GLSurfaceView.Renderer {
    private AFilter mFilter;
    private Bitmap bitmap;
    private int width,height;
    private boolean refreshFlag=false;
    private EGLConfig config;

    public SGLRender(Context mContext){
        mFilter=new ContrastColorFilter(mContext, ColorFilter.Filter.NONE);
        try {
            bitmap = BitmapFactory.decodeStream(BaseApplication.getContext().getResources().getAssets().open("texture/fengj.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mFilter.setBitmap(bitmap);
    }
    public void setFilter(AFilter filter){
        refreshFlag=true;
        mFilter=filter;
        if(bitmap!=null){
            mFilter.setBitmap(bitmap);
        }
    }
    public AFilter getFilter(){
        return mFilter;
    }
    public void refresh(){
        refreshFlag=true;
    }
//    public void setImageBuffer(int[] buffer,int width,int height){
//        bitmap= Bitmap.createBitmap(buffer,width,height, Bitmap.Config.RGB_565);
//        mFilter.setBitmap(bitmap);
//    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        this.config=config;
        mFilter.onSurfaceCreated(gl, config);
    }
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width=width;
        this.height=height;
        mFilter.onSurfaceChanged(gl, width, height);
    }
    @Override
    public void onDrawFrame(GL10 gl) {
        if(refreshFlag&&width!=0&&height!=0){
            mFilter.onSurfaceCreated(gl, config);
            mFilter.onSurfaceChanged(gl,width,height);
            refreshFlag=false;
        }
        mFilter.onDrawFrame(gl);
    }
}
