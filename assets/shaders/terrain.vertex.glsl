
// attributes of this vertex
attribute vec4 a_position;
attribute vec2 a_texCoord0;


uniform sampler2D u_emissiveTexture;
uniform sampler2D u_normalTexture;
uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;
uniform vec4 u_cameraPosition;

uniform int u_heightMapSize;    // in vertices
uniform float u_scale;
uniform float u_amplitude;
uniform float u_time;

varying vec2 v_diffuseUV;
varying float v_fog;
varying float v_lightDiffuse;

void main() {
	vec4 worldPos = u_worldTrans * a_position;

    float terrainWorldSize = float(u_heightMapSize) * u_scale;

    // offset by 0.5 because terrain is centred on origin
    vec2 UV = (worldPos.xz / terrainWorldSize) + vec2(0.5);
    //float heightSample = (UV.x < 0.0 || UV.x > 1.0 || UV.y < 0.0 || UV.y > 1.0) ? 0.0 : texture2D(u_emissiveTexture, UV).r;
    float heightSample = texture2D(u_emissiveTexture, UV).r;

    v_diffuseUV = UV;

    //vec3 normalVector = normalize(vec3(sin(UV.x*10), 2.0, cos(UV.y*5)));
    vec3 normalVector = normalize(texture2D(u_normalTexture, UV).rbg);
    normalVector = normalize(2.0 * normalVector - 1.0);
    vec3 lightDir = normalize(vec3(sin(u_time), 1.0, cos(u_time)));
    v_lightDiffuse = clamp(dot(lightDir, normalVector), 0.0, 1.0);

	worldPos.y = u_amplitude * heightSample;
	//worldPos.y = 8.0 * sin(worldPos.x/3.0) * cos(worldPos.z/2.0);


    vec3 flen = u_cameraPosition.xyz - worldPos.xyz;
    float fog = dot(flen, flen) * u_cameraPosition.w;
    v_fog = min(fog, 1.0);

   	gl_Position = u_projViewTrans * worldPos;
}
