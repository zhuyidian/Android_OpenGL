package opengl.tools;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import opengl.view.OpenGLRendererGrid;

public class OpenGLPoint {
	private FloatBuffer vertexBuffer;
	private int rows =0;
	private int clums =0;
	OpenGLRendererGrid mOpenGLRenderer = new OpenGLRendererGrid();
	private float vertices[];

	//׼����������
    private void init(){
    	int k =0;
    	vertices=new float[(rows+1)*(clums+1)*3];
    	for (int i = 0; i < rows+1; i++)
    	{
    		for (int j = 0; j < clums+1; j++)
    		{
    			try
    			{
    				vertices[k] = (float) ((2.0/(float)rows)*i - 1);
    				k++;
    				vertices[k] = (float) ((2.0/clums)*(clums - j)-1);
    				k++;
    				vertices[k] = 0;
    				k++;	
    				
    			} 
    			catch (Exception e)
    			{
    				// TODO: handle exception
    			}
    			
    		}
    	}
    	
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = vertexByteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }
    public OpenGLPoint(){
    	rows = mOpenGLRenderer.getRows();
    	clums = mOpenGLRenderer.getClums();
        init();
    }
    public void draw(GL10 gl){
        //��ʼ���鹦�� GL_VERTEX_ARRAY
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        //�趨��ɫֵ �˴�Ϊ��  (red, green, blue, alpha)~[0.0f-1.0f]
        gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
        // ָ����������
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        //���õ�Ĵ�С Ϊ 8 ����
        gl.glPointSize(8f);
        // ���Ƶ�  GL_POINTS �� vertices.length/3 ��ĸ���
        gl.glDrawArrays(GL10.GL_POINTS, 0, vertices.length / 3);
        //�ر����鹦�� GL_VERTEX_ARRAY
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}