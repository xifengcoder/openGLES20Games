uniform mat4 uMVPMatrix;        // A constant representing the combined model/view/projection matrix.
uniform mat4 uMVMatrix;    // A constant representing the combined model/view matrix.

attribute vec4 aPosition;        // Per-vertex position information we will pass in.
attribute vec4 aColor;            // Per-vertex color information we will pass in.
attribute vec3 aNormal;		// Per-vertex normal information we will pass in.
attribute vec2 aTexCoordinate; // Per-vertex texture coordinate information we will pass in.

varying vec4 vColor;        // This will be passed into the fragment shader.
varying vec2 vTexCoordinate;  // This will be passed into the fragment shader.
varying vec3 vNormal;			// This will be passed into the fragment shader.
varying vec3 vPosition;		// This will be passed into the fragment shader.

// The entry point for our vertex shader.
void main()
{
    vPosition = vec3(uMVMatrix * aPosition);  // Transform the vertex into eye space.
    vColor = aColor; // Pass through the color.
    vTexCoordinate = aTexCoordinate; // Pass through the texture coordinate.
    vNormal = vec3(uMVMatrix * vec4(aNormal, 0.0)); // Transform the normal's orientation into eye space.

    // gl_Position is a special variable used to store the final position.
    // Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
    gl_Position = uMVPMatrix * aPosition;
}