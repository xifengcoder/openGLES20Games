uniform mat4 uMVPMatrix;        // A constant representing the combined model/view/projection matrix.

attribute vec3 aPosition;        // Per-vertex position information we will pass in.
attribute vec4 aColor;            // Per-vertex color information we will pass in.
attribute vec2 aTexCoordinate; // Per-vertex texture coordinate information we will pass in.

varying vec4 vColor;        // This will be passed into the fragment shader.
varying vec2 vTexCoordinate;  // This will be passed into the fragment shader.

// The entry point for our vertex shader.
void main()
{
    vColor = aColor;     // Pass through the color.
    vTexCoordinate = aTexCoordinate;  // Pass through the texture coordinate.

    // gl_Position is a special variable used to store the final position.
    // Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
    gl_Position = uMVPMatrix * vec4(aPosition, 1);
}