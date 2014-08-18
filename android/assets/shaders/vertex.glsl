attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_modelMatrix, u_modelViewMatrix, u_modelViewProjectionMatrix;
uniform mat3 u_normalMatrix;
uniform vec3 u_cameraPosition;
uniform float u_shininess;

varying vec2 v_texCoord0;
varying vec3 v_normal, v_viewDirection;
varying vec3 v_viewSpacePosition;

void main() {
    vec4 position = vec4(a_position, 1.0);

    v_normal = u_normalMatrix * a_normal;
    v_texCoord0 = a_texCoord0;

    gl_Position = u_modelViewProjectionMatrix * position;

    v_viewSpacePosition = (u_modelViewMatrix * position).xyz;

    v_viewDirection = normalize(u_cameraPosition - (u_modelMatrix * position).xyz);
}