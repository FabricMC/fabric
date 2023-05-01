#version 150

#moj_import "test_local_import.glsl"

in vec3 Position;

uniform mat4 ProjMat;
uniform mat4 ModelViewMat;

void main() {
    gl_Position = applyMatrix(ProjMat, applyMatrix(ModelViewMat, vec4(Position, 1.0)));
}
