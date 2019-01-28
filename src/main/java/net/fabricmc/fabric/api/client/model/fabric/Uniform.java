package net.fabricmc.fabric.api.client.model.fabric;

import net.minecraft.client.util.math.Matrix4f;

public interface Uniform {
    @FunctionalInterface
    public interface Uniform1f extends Uniform {
        void set(float v0);
    }
    
    @FunctionalInterface
    public interface Uniform2f extends Uniform {
        void set(float v0, float v1);
    }
    
    @FunctionalInterface
    public interface Uniform3f extends Uniform {
        void set(float v0, float v1, float v2);
    }
    
    @FunctionalInterface
    public interface Uniform4f extends Uniform {
        void set(float v0, float v1, float v2, float v3);
    }
    
    @FunctionalInterface
    public interface Uniform1i extends Uniform {
        void set(int v0);
    }
    
    @FunctionalInterface
    public interface Uniform2i extends Uniform {
        void set(int v0, int v1);
    }
    
    @FunctionalInterface
    public interface Uniform3i extends Uniform {
        void set(int v0, int v1, int v2);
    }
    
    @FunctionalInterface
    public interface Uniform4i extends Uniform {
        void set(int v0, int v1, int v2, int v3);
    }
    
    public interface UniformMatrix4f extends Uniform {
        void set(float... elements);

        void set(Matrix4f matrix);
    }
}
