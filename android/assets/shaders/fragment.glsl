#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord0;
varying vec3 v_lightDiffuse;
varying vec3 v_lightSpecular;

uniform vec4 u_ambientColor;
uniform vec4 u_diffuseColor;
uniform vec4 u_specularColor;

void main() {
    //Calculate the final color value from ambient, diffuse and specular lighting
    gl_FragColor = u_ambientColor * u_diffuseColor + u_diffuseColor * vec4(v_lightDiffuse, 1) + u_specularColor * vec4(v_lightSpecular, 1);
}