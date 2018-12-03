precision mediump float;  //设置工作精度
uniform sampler2D vTexture
varying vec2 aCoordinate;

void main()                         
{                       
   gl_FragColor=texture2D(vTexture,aCoordinate);
}