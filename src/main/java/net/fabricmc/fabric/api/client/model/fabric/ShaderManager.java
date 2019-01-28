package net.fabricmc.fabric.api.client.model.fabric;

import java.util.function.Consumer;

import net.fabricmc.fabric.api.client.model.fabric.Uniform.Uniform1f;
import net.fabricmc.fabric.api.client.model.fabric.Uniform.Uniform1i;
import net.fabricmc.fabric.api.client.model.fabric.Uniform.Uniform2f;
import net.fabricmc.fabric.api.client.model.fabric.Uniform.Uniform2i;
import net.fabricmc.fabric.api.client.model.fabric.Uniform.Uniform3f;
import net.fabricmc.fabric.api.client.model.fabric.Uniform.Uniform3i;
import net.fabricmc.fabric.api.client.model.fabric.Uniform.Uniform4f;
import net.fabricmc.fabric.api.client.model.fabric.Uniform.Uniform4i;
import net.fabricmc.fabric.api.client.model.fabric.Uniform.UniformMatrix4f;

/**
 * Allows creation of shader-based custom renders using standard materials
 * to control vertex attribute bindings and lighting model interaction.<p>
 * 
 * Also handles creation of uniforms via one of the provided "uniform..." methods.
 * Uniforms can never be directly obtained - this prevents abuse of uniforms that
 * would damage performance due to excessive updates. (Per-block uniforms, for example,
 * are not possible.)<p>
 * 
 * Using a uniform in shader is simple: reference the uniform by name in your 
 * declaration. The renderer will ensure the uniforms is bound and updated appropriately.<p>
 * 
 * Uniforms share the same name-space. This simplifies implementations by avoiding the
 * need to rename/rebind conflicting uniform names in the renderer.  Use a prefix or 
 * suffix to ensure uniqueness and/or publish names of uniforms that should be reused.<p>
 * 
 * Several uniforms are expected to be part of any renderer implementation that supports
 * shaders. Their names are listed below as string constants with explanations of function.
 */
public interface ShaderManager {
    /**
     * {@link UniformMatrix4f} with current model view matrix. Binding: u_modelView. Refreshed on load.
     */
    public static String UNIFORM_MODEL_VIEW = "u_modelView";
    
    /**
     * {@link UniformMatrix4f} with current model view projection matrix. Binding: u_modelViewProjection. Refresh on load.
     */
    public static String UNIFORM_MODEL_VIEW_PROJECTION = "u_modelViewProjection";

    /**
     * {@link UniformMatrix4f} with current projection matrix. Binding: u_projection. Refresh on load.
     */
    public static String UNIFORM_PROJECTION = "u_projection";
    
    /**
     * {@link Uniform1f} with current game time for animation. Refreshed every frame.
     */
    public static String UNIFORM_TIME = "u_time";
    
    /**
     * {@link Uniform3f} with viewing entity eye position. Refreshed every frame.
     */
    public static String UNIFORM_EYE_POSITION = "u_eye_position";
    
    /**
     * {@link Uniform3f} with fog parameters: end, end-start, and density. Zero density means linear fog.
     * Refreshed every tick.
     */
    public static String UNIFORM_FOG_ATTRIBUTES = "u_fogAttributes";
    
    /**
     * {@link Uniform3f} with current fog color. Refreshed every tick.
     */
    public static String UNIFORM_FOG_COLOR = "u_fogColor";

    /**
     * Creates a new material using a standard material plus shaders. The standard material controls
     * how much information will be sent to the shader (via texture depth) and if/how/which vertex colors
     * will be modified by lighting.<p>
     * 
     * See {@link QuadMaker} for standard vertex attribute binding names. Uniforms will be automatically
     * associated based on header declarations.<p>
     * 
     * @param standard  Must be a standard material.
     * @param vertexSource  GLSL 120 vertex shader source. Renderer will strip redundant header declarations.
     * @param fragmentSource GLSL 120 vertex shader source. Renderer will strip redundant header declarations.
     * @param flags  TBD - flags to control other shader configuration
     * 
     * @return The new material.  Material will act as a standard material if source compilation fails.
     */
    RenderMaterial shaderMaterial(RenderMaterial standard, String vertexSource, String fragmentSource, int flags);
    
    /**
     * Creates a new uniform. See {@link ShaderManager} header for additional info.
     */
    boolean uniform1f(String name, UniformRefreshFrequency frequency, Consumer<Uniform1f> initializer);

    /**
     * Creates a new uniform. See {@link ShaderManager} header for additional info.
     */
    boolean uniform2f(String name, UniformRefreshFrequency frequency, Consumer<Uniform2f> initializer);

    /**
     * Creates a new uniform. See {@link ShaderManager} header for additional info.
     */
    boolean uniform3f(String name, UniformRefreshFrequency frequency, Consumer<Uniform3f> initializer);

    /**
     * Creates a new uniform. See {@link ShaderManager} header for additional info.
     */
    boolean uniform4f(String name, UniformRefreshFrequency frequency, Consumer<Uniform4f> initializer);

    /**
     * Creates a new uniform. See {@link ShaderManager} header for additional info.
     */
    boolean uniform1i(String name, UniformRefreshFrequency frequency, Consumer<Uniform1i> initializer);

    /**
     * Creates a new uniform. See {@link ShaderManager} header for additional info.
     */
    boolean uniform2i(String name, UniformRefreshFrequency frequency, Consumer<Uniform2i> initializer);

    /**
     * Creates a new uniform. See {@link ShaderManager} header for additional info.
     */
    boolean uniform3i(String name, UniformRefreshFrequency frequency, Consumer<Uniform3i> initializer);

    /**
     * Creates a new uniform. See {@link ShaderManager} header for additional info.
     */
    boolean uniform4i(String name, UniformRefreshFrequency frequency, Consumer<Uniform4i> initializer);

    /**
     * Creates a new uniform for this pipeline. See {@link UniformUpdateFrequency} for additional info.
     */
    boolean uniformMatrix4f(String name, UniformRefreshFrequency frequency, Consumer<UniformMatrix4f> initializer);
}
