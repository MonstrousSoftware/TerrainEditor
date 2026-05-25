#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_diffuseTexture;
uniform sampler2D u_normalTexture;
uniform vec4 u_fogColor;

varying vec2 v_diffuseUV;
varying float v_fog;
varying float v_lightDiffuse;   // diffuse lighting level


void main() {
    vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV);

    vec3 diffuseColor = diffuse.rgb * v_lightDiffuse;
    //gl_FragColor = vec4(diffuseColor, 1.0);
    gl_FragColor.rgb = mix(diffuseColor.rgb, u_fogColor.rgb, v_fog);
}
