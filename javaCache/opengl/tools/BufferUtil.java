package opengl.tools;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

///函数转换array到Buffer
public class BufferUtil {
	public static FloatBuffer mBuffer;

	public static FloatBuffer floatToBuffer(float[] a) {
		//先初始化Buffer，数组的长度*4，因为一个Float占4个字节
		ByteBuffer mbb = ByteBuffer.allocateDirect(a.length*4);
		//数组排序用NativaOrder
		mbb.order(ByteOrder.nativeOrder());
		
		mBuffer = mbb.asFloatBuffer();
		mBuffer.put(a);
		mBuffer.position(0);
		return mBuffer;
	}
}