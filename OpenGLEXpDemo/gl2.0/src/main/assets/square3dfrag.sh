precision mediump float;  //设置工作精度
varying vec4 v_Color; //接收从顶点着色器过来的参数

void main()                         
{                       
   gl_FragColor = v_Color;
}