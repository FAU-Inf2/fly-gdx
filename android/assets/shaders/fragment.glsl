#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord0;
uniform vec4 u_diffuseColor;
void main() {
    gl_FragColor = u_diffuseColor;
}