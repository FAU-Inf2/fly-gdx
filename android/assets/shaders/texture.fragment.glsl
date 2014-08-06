#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord0;
varying vec3 v_lightDiffuse;
varying vec3 v_lightSpecular;

uniform vec4 u_specularColor;
uniform sampler2D texture1;

void main() {
    gl_FragColor.xyz = texture2D(texture1, v_texCoord0).xyz * v_lightDiffuse + u_specularColor.xyz * v_lightSpecular;
}