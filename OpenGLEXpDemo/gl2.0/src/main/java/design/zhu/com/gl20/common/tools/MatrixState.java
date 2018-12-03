package design.zhu.com.gl20.common.tools;

import android.opengl.Matrix;

public class MatrixState {
    // 获取具体物体的总变换矩阵
    private static float[] mMVPMatrix = new float[16]; //总变换矩阵
    private static float[] mProjMatrix = new float[16];// 4x4矩阵 存储投影矩阵   投影矩阵
    private static float[] mVMatrix = new float[16];// 摄像机位置朝向9参数矩阵    相机视口矩阵
    /*
     * 这个变换矩阵 在设置变换 , 位移 , 旋转的时候 将参数设置到这个矩阵中去
     */
    private static float[] mMMatrix = new float[16];			//具体物体的3D变换矩阵, 包括旋转, 平移, 缩放

    // 设置摄像机
    public static float[] setCamera(float cx, // 摄像机位置x
                                 float cy, // 摄像机位置y
                                 float cz, // 摄像机位置z
                                 float tx, // 摄像机目标点x
                                 float ty, // 摄像机目标点y
                                 float tz, // 摄像机目标点z
                                 float upx, // 摄像机UP向量X分量
                                 float upy, // 摄像机UP向量Y分量
                                 float upz // 摄像机UP向量Z分量
    ) {
        Matrix.setLookAtM(mVMatrix, 0, cx, cy, cz, tx, ty, tz, upx, upy, upz);
        return mVMatrix;
    }

    // 设置透视投影参数
    /**
     * 使用透视投影，物体离视点越远，呈现出来的越小。离视点越近，呈现出来的越大
     * @param left
     * @param right
     * @param bottom
     * @param top
     * @param near
     * @param far
     * @return
     */
    public static float[] setProjectFrustum(float left, // near面的left
                                         float right, // near面的right
                                         float bottom, // near面的bottom
                                         float top, // near面的top
                                         float near, // near面距离
                                         float far // far面距离
    ) {
        Matrix.frustumM(mProjMatrix,   //接收透视投影的变换矩阵
                         0, //变换矩阵的起始位置（偏移量）
                            left,//变换矩阵的起始位置（偏移量）
                            right,  //相对观察点近面的右边距
                            bottom, //相对观察点近面的下边距
                            top,//相对观察点近面的上边距
                            near,//相对观察点近面距离
                            far);//相对观察点远面距离
        return mProjMatrix;
    }

    /**
     * 使用正交投影，物体呈现出来的大小不会随着其距离视点的远近而发生变化
     * @param left
     * @param right
     * @param bottom
     * @param top
     * @param near
     * @param far
     * @return
     */
    public static float[] orthoM(
            float left,         //相对观察点近面的左边距
            float right,        //相对观察点近面的右边距
            float bottom,       //相对观察点近面的下边距
            float top,          //相对观察点近面的上边距
            float near,         //相对观察点近面距离
            float far          //相对观察点远面距离
    ){
        Matrix.orthoM (mProjMatrix,          //接收正交投影的变换矩阵
        0,        //变换矩阵的起始位置（偏移量）
        left,         //相对观察点近面的左边距
        right,        //相对观察点近面的右边距
        bottom,       //相对观察点近面的下边距
        top,          //相对观察点近面的上边距
        near,         //相对观察点近面距离
        far);          //相对观察点远面距离
        return mProjMatrix;
    }

    public static float[] getFinalMatrix() {
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        return mMVPMatrix;
    }

    /**
     * 设置旋转变化矩阵
     */
    public static float[] setRotateM(float a, //旋转角度
                                     float x, //旋转轴X坐标
                                     float y, //旋转轴Y坐标
                                     float z  //旋转轴Z坐标
    ){
        /*
         * 设置旋转变化矩阵
         * 参数介绍 : ① 3D变换矩阵 ② 矩阵数组的起始索引 ③旋转的角度 ④⑤⑥
         *
         * 设置旋转初始情况
            Matrix.setRotateM(float[] rm, int rmOffset, float a, float x, float y, float z)
            参数 : rm 变换矩阵; rmOffset 变换矩阵的索引; a 旋转角度; 剩下的三个是旋转的轴
            这个方法的作用是设置旋转变化矩阵
         */
        Matrix.setRotateM(mMMatrix, 0, a, x, y, z);
        return mMMatrix;
    }

    /**
     * 设置位移
     */
    public static float[] translateM(float x, //位移向量.X
                                     float y, //位移向量.Y
                                     float z  //位移向量.Z
    ){
        /*
		 * 设置沿z轴正方向位移
		 * 参数介绍 : ① 变换矩阵 ② 矩阵索引开始位置 ③④⑤设置位移方向z轴
		 *
		 * 设置位移
			 Matrix.translateM(float[] m, int mOffset, float x, float y, float z)
			参数 : m 变换矩阵; mOffset 变换矩阵的起始位置; 剩下的三个是位移向量.
		 */
        Matrix.translateM(mMMatrix, 0, x, y, z);
        return mMMatrix;
    }

    /**
     * 设置位移
     */
    public static float[] rotateM(float a, //a 旋转的角度
                                  float x, //旋转的轴 X
                                  float y, //旋转的轴 Y
                                  float z  //旋转的轴 Z
    ){
       /*
		 * 设置绕x轴旋转
		 * 参数介绍 : ① 变换矩阵 ② 索引开始位置 ③ 旋转角度 ④⑤⑥ 设置绕哪个轴旋转
		 *
		 * 设置旋转矩阵
			 Matrix.rotateM(float[] m, int mOffset, float a, float x, float y, float z)
			参数 : m 变换矩阵; mOffset 变换矩阵起始位置; a 旋转的角度; 剩下的三个参数是旋转的轴;
		 */
        Matrix.rotateM(mMMatrix, 0, a, x, y, z);
        return mMMatrix;
    }
}
