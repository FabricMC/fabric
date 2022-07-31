package net.fabricmc.fabric.api.client.render.fluid.v1;

import net.minecraft.client.render.FogShape;

/**
 * Provides fog rendering parameters.
 *
 * @param fogStart Distance in blocks, from the camera position, in which the fog starts rendering.
 * @param fogEnd   Distance in blocks, from the camera position, after which the fog is totally opaque.
 * @param fogShape Shape of the fog.
 */
public record FogParameters(float fogStart, float fogEnd, FogShape fogShape) {
}
