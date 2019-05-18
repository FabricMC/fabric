package net.fabricmc.indigo.renderer.accessor;

import net.fabricmc.indigo.renderer.render.TerrainRenderContext;

/**
 * Used to stash block renderer reference in local scope during
 * chunk rebuild, thus avoiding repeated thread-local lookups.
 */
public interface AccessChunkRendererRegion {
    TerrainRenderContext fabric_getRenderer();
    void fabric_setRenderer(TerrainRenderContext renderer);
}
