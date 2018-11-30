package opengl.tools.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class DrawPoint{
	static float[] vertexArray = new float[]{
			-0.8f , -0.4f * 1.732f , 0.0f ,
			0.8f , -0.4f * 1.732f , 0.0f ,
			0.0f , 0.4f * 1.732f , 0.0f ,
		};

	public static void DrawScene(GL10 gl) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertexArray.length*4);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer vertex = vbb.asFloatBuffer();
        vertex.put(vertexArray);
        vertex.position(0);

        gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
        gl.glPointSize(8f);
        gl.glLoadIdentity(); 
        gl.glTranslatef(0, 0, -4);

    	gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    	
    	gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertex);
    	gl.glDrawArrays(GL10.GL_POINTS, 0, 3);

     	gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
}
