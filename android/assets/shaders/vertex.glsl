attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform vec3 u_dirLightDirection;
uniform vec4 u_dirLightColor;
uniform mat3 u_normalMatrix;
uniform float u_shininess;
uniform vec3 u_cameraPosition;
uniform float u_ambientFactor;

varying vec2 v_texCoord0;
varying vec3 v_lightDiffuse;
varying vec3 v_lightSpecular;

void main() {
    v_texCoord0 = a_texCoord0;
    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);

    //Calculate the diffuse lighting for our single directional light source
    vec3 lightDir = -normalize(u_dirLightDirection);
    vec3 normal = u_normalMatrix * a_normal;
    v_lightDiffuse = u_dirLightColor.xyz * clamp(dot(normal, lightDir), 0.0, 1.0);

    //Calculate the specular highlight
    vec3 viewVec = normalize(u_cameraPosition - (u_worldTrans * vec4(a_position, 1.0)).xyz);
    vec3 r = 2.0 * dot(normal, lightDir) * normal - lightDir;
    float rDotView = max(0.0, dot(viewVec, r));
    v_lightSpecular = u_dirLightColor.xyz * max(0.0, pow(rDotView, u_shininess));
    }