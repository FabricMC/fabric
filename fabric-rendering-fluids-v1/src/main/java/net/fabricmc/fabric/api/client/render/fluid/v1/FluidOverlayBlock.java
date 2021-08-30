package net.fabricmc.fabric.api.client.render.fluid.v1;

import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.TransparentBlock;

/**
 * Implement this in your {@link Block} to tell the fluid renderer that your block wants the overlay texture for water
 * and other fluids having overlay textures. If your block is a subclass of {@link TransparentBlock} or {@link
 * LeavesBlock}, you do not need this interface.
 *
 * <p>This interface can be helpful if your block already inherits from another block type and cannot inherit
 * {@link TransparentBlock} anymore.
 */
public interface FluidOverlayBlock {
}
