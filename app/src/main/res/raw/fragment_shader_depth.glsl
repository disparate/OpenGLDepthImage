#ifdef GL_ES
  precision mediump float;
#endif

uniform vec2 resolution;
uniform vec2 mouse;
uniform vec2 threshold;
uniform float pixelRatio;
uniform sampler2D depthImage;
uniform sampler2D originalImage;


 vec2 mirrored(vec2 v) {
  vec2 m = mod(v,2.);
  return mix(m,2.0 - m, step(1.0 ,m));
}

 void main() {
  vec2 uv = gl_FragCoord.xy / resolution.xy ;


  uv.y = 1. - uv.y;
  //vec4 depth = texture2D(depthImage,mirrored(vUv));

  vec4 depth = texture2D(depthImage, mirrored(uv));
  vec2 fake3d = vec2(uv.x + (depth.r - 0.5)*mouse.x/threshold.x, uv.y + (depth.r - 0.5)*mouse.y/threshold.y);
  gl_FragColor = texture2D(originalImage, mirrored(fake3d));
}

