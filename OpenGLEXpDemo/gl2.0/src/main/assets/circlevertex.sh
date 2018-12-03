uniform mat4 vMatrix; //总变换矩阵
attribute vec4 vPosition;  //顶点位置

void main()
{
   gl_Position = vMatrix * vPosition;
}                      