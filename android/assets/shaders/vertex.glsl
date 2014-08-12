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

#ifdef numDirLights
    struct dirLight {
        vec3 direction;
        vec4 color;
    };

    uniform dirLight u_dirLights[numDirLights];
#endif

#ifdef numPointLights
    struct pointLight {
        vec3 position;
        vec4 color;
    };

    uniform pointLight u_pointLights[numPointLights];
#endif

varying vec2 v_texCoord0;
varying vec3 v_lightDiffuse;
varying vec3 v_lightSpecular;

void main() {
    v_texCoord0 = a_texCoord0;
    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);

    v_lightDiffuse = vec3(0);
    vec3 viewVec = normalize(u_cameraPosition - (u_worldTrans * vec4(a_position, 1.0)).xyz);
    vec3 normal = u_normalMatrix * a_normal;

    #ifdef numDirLights
    //Calculate the diffuse lighting for directional light sources
        for(int i=0; i<numDirLights; i++) {
            vec3 lightDir = -normalize(u_dirLights[i].direction);
            vec3 value = u_dirLights[i].color.xyz * clamp(dot(normal, lightDir), 0.0, 1.0);
            v_lightDiffuse += value;
            //vec3 r = 2.0 * dot(normal, lightDir) * normal - lightDir;
            //float rDotView = max(0.0, dot(viewVec, r));
            float halfDotView = max(0.0, dot(normal, normalize(lightDir + viewVec)));
            v_lightSpecular += value * max(0.0, pow(halfDotView, u_shininess));
        }
    #endif

    #ifdef numPointLights
    //Calculate the diffuse lighting for point lights
        for(int i=0; i<numPointLights; i++) {
            vec3 lightDir = normalize(u_pointLights[i].position - (u_worldTrans * vec4(a_position, 1.0)).xyz);
            vec3 value = u_pointLights[i].color.xyz * clamp(dot(normal, lightDir), 0.0, 1.0);
            v_lightDiffuse += value;
            //vec3 r = 2.0 * dot(normal, lightDir) * normal - lightDir;
            //float rDotView = max(0.0, dot(viewVec, r));
            float halfDotView = max(0.0, dot(normal, normalize(lightDir + viewVec)));
            v_lightSpecular += value * max(0.0, pow(halfDotView, u_shininess));
        }
    #endif
}