precision mediump float;        // Set the default precision to medium. We don't need as high of a
// precision in the fragment shader.
uniform sampler2D uTexture;    // The input texture.

varying vec4 vColor;            // This is the color from the vertex shader interpolated across the triangle per fragment.
varying vec2 vTexCoordinate;   // Interpolated texture coordinate per fragment.

// The entry point for our fragment shader.
void main()
{
    // Multiply the color by the diffuse illumination level and texture value to get final output color.
    //gl_FragColor = (vColor * diffuse * texture2D(uTexture, vTexCoordinate));
    gl_FragColor = (vColor * texture2D(uTexture, vTexCoordinate));
}

