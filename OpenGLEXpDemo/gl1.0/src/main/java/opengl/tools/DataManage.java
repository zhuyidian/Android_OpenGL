package opengl.tools;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import opengl.view.R;

public class DataManage {
	public static Bitmap mBitmap;
	
	public static void init(Resources res) {
		mBitmap = BitmapFactory.decodeResource(res, R.drawable.b);
	}
	public static Bitmap getBitmap() {
		return mBitmap;
	}
}