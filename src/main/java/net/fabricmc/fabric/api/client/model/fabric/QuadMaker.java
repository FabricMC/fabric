package net.fabricmc.fabric.api.client.model.fabric;

/**
 * Specialized {@link MutableQuadView} for static mesh building.<p>
 * 
 * Instances of {@link MutableQuadView} will practically always be
 * threadlocal and/or reused - do not retain references.
 */
public interface QuadMaker extends MutableQuadView {
    /**
     * In static mesh building, causes quad to be appended to the mesh being built.
     * In a dynamic render context, create a new quad to be output to rendering.
     * In both cases, invalidates the current instance.
     */
    void emit();
}
