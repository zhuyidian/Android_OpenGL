package opengl.tools;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import opengl.view.OpenGLRendererGrid;

public class OpenGLLine {
	private int rows =0;
	private int clums =0;
	OpenGLRendererGrid mOpenGLRenderer = new OpenGLRendererGrid();
	private float vertices[][];
	private FloatBuffer[] faceVertexBuffer;
	private float yvertices[][];
	private FloatBuffer[] yfaceVertexBuffer;

	//׼����������
    private void init(){
    	int k = 0;
    	
    	vertices=new float[rows+1][(clums+1)*3];
    	yvertices = new float[rows+1][(clums+1)*3];
    	for (int i = 0; i < rows+1; i++) {
    		k = 0;
    		for (int j = 0; j < clums+1; j++) {
    			try {
    				vertices[i][k] = (float) ((2.0/(float)clums)*j - 1);
    				k++;
    				vertices[i][k] = (float) ((2.0/rows)*(rows - i)-1);
    				k++;
    				vertices[i][k] = 0;
    				k++;
    			} 
    			catch (Exception e) {
    				// TODO: handle exception
    			}
    		}
    	}
    	for (int i = 0; i < clums+1; i++) {
    		k = 0;
    		for (int j = 0; j < rows+1; j++) {
    			try {
    				yvertices[i][k] = (float) ((2.0/(float)rows)*i - 1);
    				k++;
    				yvertices[i][k] = (float) ((2.0/clums)*(clums - j)-1);
    				k++;
    				yvertices[i][k] = 0;
    				k++;	
    				
    			} 
    			catch (Exception e) {
    				// TODO: handle exception
    			}
    		}
    	}

    	faceVertexBuffer = new FloatBuffer[rows+1];
    	for (int j = 0; j < vertices.length; j++) {
			ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices[j].length * 4);
	        vertexByteBuffer.order(ByteOrder.nativeOrder());
	        faceVertexBuffer[j] = vertexByteBuffer.asFloatBuffer();
	        faceVertexBuffer[j].put(vertices[j]);
	        faceVertexBuffer[j].position(0);
		}
    	
    	yfaceVertexBuffer = new FloatBuffer[rows+1];
    	for (int j = 0; j < yvertices.length; j++) {
			ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(yvertices[j].length * 4);
	        vertexByteBuffer.order(ByteOrder.nativeOrder());
	        yfaceVertexBuffer[j] = vertexByteBuffer.asFloatBuffer();
	        yfaceVertexBuffer[j].put(yvertices[j]);
	        yfaceVertexBuffer[j].position(0);
		}
    }
    public OpenGLLine() {
    	rows = mOpenGLRenderer.getRows();
    	clums = mOpenGLRenderer.getClums();
        init();
    }
    public void draw(GL10 gl) {
        //��ʼ���鹦�� GL_VERTEX_ARRAY
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        //�趨��ɫֵ �˴�Ϊ��  (red, green, blue, alpha)~[0.0f-1.0f]
        gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
        gl.glLineWidthx(2);
        // ָ����������
        for (int j = 0; j < rows+1; j++) {
	        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, faceVertexBuffer[j]);
	
	        gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, clums+1);
	        //�ر����鹦�� GL_VERTEX_ARRAY
	        
		}
        for (int j = 0; j < clums+1; j++) {
	        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, yfaceVertexBuffer[j]);
	
	        gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, rows+1);
	        //�ر����鹦�� GL_VERTEX_ARRAY
	        
		}
        
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
}