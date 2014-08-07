#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord0;
varying vec3 v_lightDiffuse;
varying vec3 v_lightSpecular;

uniform vec4 u_ambientLight;
uniform vec4 u_specularColor;
uniform sampler2D texture1;

void main() {
    vec4 color = texture2D(texture1, v_texCoord0);
    gl_FragColor.xyz = u_ambientLight.xyz * color.xyz + color.xyz * v_lightDiffuse + u_specularColor.xyz * v_lightSpecular;
}