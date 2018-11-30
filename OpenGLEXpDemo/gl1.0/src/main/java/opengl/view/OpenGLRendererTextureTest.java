package opengl.view;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import opengl.tools.mesh.Group;
import opengl.tools.mesh.Mesh;

/**
 具体在GlSurfaceView.Renderer中的绘制步骤：
 第一步：设置视图展示窗口(viewport) :在onSurfaceChanged中调用GLES20.glViewport(0, 0, width, height);
 第二步：创建图形类，确定好顶点位置和图形颜色，将顶点和颜色数据转换为OpenGl使用的数据格式
 第三步：加载顶点找色器和片段着色器用来修改图形的颜色，纹理，坐标等属性
 第四步：创建投影和相机视图来显示视图的显示状态，并将投影和相机视图的转换传递给着色器。
 第五步：创建项目(Program),连接顶点着色器片段着色器。
 第六步：将坐标数据传入到OpenGl ES程序中
 */
//渲染类
public class OpenGLRendererTextureTest implements Renderer {
	Group root;

	public OpenGLRendererTextureTest(){
		Group group = new Group();
		root = group;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		// 清除屏幕和深度缓存
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// 在编译运行，这次倒是有显示了，当正方形迅速后移直至看不见，这是因为每次调用onDrawFrame 时，每次都再向后移动4个单位，需要加上重置Matrix的代码。
		gl.glLoadIdentity();
		// 来绘制这个正方形，编译运行，什么也没显示，这是为什么呢？这是因为OpenGL ES从当前位置开始渲染，缺省坐标为(0,0,0)，和View port 的坐标一样，相当于把画面放在眼前，
		// 对应这种情况OpenGL不会渲染离view Port很近的画面，因此我们需要将画面向后退一点距离。
		gl.glTranslatef(0, 0, -4);

		root.draw(gl);
	}
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		// 将当前视图端口设置为新大小
		gl.glViewport(0, 0, width, height);
		// 选择投影矩阵
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// 重置投影矩阵
		gl.glLoadIdentity();
		// 计算窗口的纵横比
		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
		// 选择模型视图矩阵
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		// 重置模型视图矩阵
		gl.glLoadIdentity();
	}
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		// 格式GRBA 将背景颜色设置成黑色
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		//gl.glClearColor(0.0f, 1.0f, 1.0f, 0.5f);  //蓝色背景
		// 启用平滑着色，默认不是真的需要
		gl.glShadeModel(GL10.GL_SMOOTH);
		// 深度缓冲设置
		gl.glClearDepthf(1.0f);
		// 启用深度测试
		gl.glEnable(GL10.GL_DEPTH_TEST);
		// 要做深度测试的类型
		gl.glDepthFunc(GL10.GL_LEQUAL);
		// 非常好的透视计算
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
	}

	/**
	 * Adds a mesh to the root.
	 *
	 * @param mesh
	 *            the mesh to add.
	 */
	public void addMesh(Mesh mesh) {
		root.add(mesh);
	}
}