package net.fabricmc.fabric.api.client.model.fabric;

/**
 * Governs how often shader uniform initializers are called.<p>
 * 
 * In all cases, initializers will only be called if a shader 
 * using the uniform is activated and values are only uploaded if they have changed.
 */
public enum UniformRefreshFrequency {
    /**
     * Uniform initializer only called 1X a time of program load or reload.
     */
    ON_LOAD,

    /**
     * Uniform initializer called 1X per game tick. (20X per second)
     */
    PER_TICK,
    
    /**
     * Uniform initializer called 1X per render frame. (Variable frequency.)
     */
    PER_FRAME
}
