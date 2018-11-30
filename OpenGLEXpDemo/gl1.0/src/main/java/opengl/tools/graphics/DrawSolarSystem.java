package opengl.tools.graphics;

import android.opengl.GLU;

import javax.microedition.khronos.opengles.GL10;

public class DrawSolarSystem {
	private static Star sun=new Star();
	private static Star earth=new Star();
	private static Star moon=new Star();
	private static int angle=0;

	public static void DrawScene(GL10 gl) {
		gl.glLoadIdentity();
		//使用GLU的gluLookAt 来定义modelview Matrix
		//把相机放在正对太阳中心(0,0,0)，距离15 (0,0,15)
		GLU.gluLookAt(gl,0.0f, 0.0f, 15.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

		//首先将当前matrix 入栈，以红色绘制太阳，并逆向转动，将当前matrix 入栈的目的是在能够在绘制地球时恢复当前栈
		// Star A
		//栈顶矩阵为最初的模型视图矩阵
		gl.glPushMatrix();
		// Rotate Star A counter-clockwise.
		gl.glRotatef(angle, 0, 0, 1);
		gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		// Draw Star A.
		sun.draw(gl);
		//栈中应该没有保存坐标系,也即回到最初
		gl.glPopMatrix();

		//然后绘制地球，使用局部坐标系来想象地球和太阳之间的相对运动，地球离开一距离绕太阳公转，相当于先旋转地球的局部坐标系，然后再平移地球的局部坐标系。对应到代码为先glRotatef ,然后glTranslate.
		// Star B
		//栈顶矩阵为太阳对应的模型视图矩阵
		gl.glPushMatrix();
		// Rotate Star B before moving it,
		//making it rotate around A.
		gl.glRotatef(-angle, 0, 0, 1);
		// Move Star B.
		gl.glTranslatef(3, 0, 0);
		// Scale it to 50% of Star A
		gl.glScalef(.5f, .5f, .5f);
		gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
		// Draw Star B.
		earth.draw(gl);

		//最后是绘制月亮，使用类似的空间想象方法
		// Star C
		//栈顶矩阵为地球地球对应的模型视图矩阵
		gl.glPushMatrix();
		// Make the rotation around B
		gl.glRotatef(-angle, 0, 0, 1);
		gl.glTranslatef(2, 0, 0);
		// Scale it to 50% of Star B
		gl.glScalef(.5f, .5f, .5f);
		// Rotate around it's own center.
		gl.glRotatef(angle*10, 0, 0, 1);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		// Draw Star C.
		moon.draw(gl);
		//栈顶矩阵恢复为太阳对应的模型视图矩阵，这时可以绘制第二个地球了
		gl.glPopMatrix();
		//栈中应该没有保存坐标系,业即回到最初，这时可以绘制第二个太阳了
		gl.glPopMatrix();

		// Increse the angle.
		angle++;
	}
}
