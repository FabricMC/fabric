package net.fabricmc.fabric.test.mixin.client.rendering.fluid.mixin;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidOverlayBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.WallBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DoorBlock.class) // Makes doors next to water get overlay textures, instead of falling water texture (easier than adding a new block)
public class MixinDoorBlock implements FluidOverlayBlock {
}
