#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord0;
uniform sampler2D texture1;
void main() {
    gl_FragColor = texture2D(texture1, v_texCoord0);
}