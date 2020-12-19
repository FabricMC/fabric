package net.fabricmc.fabric.mixin.tags.extension.client.bookshelf;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.EnchantingTableBlock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.tags.v1.FabricBlockTags;

@Mixin(EnchantingTableBlock.class)
@Environment(EnvType.CLIENT)
public abstract class EnchantingTableBlockMixin {
	@Redirect(
			method = "randomDisplayTick",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"
			)
	)
	private boolean randomDisplayTick$isOf(BlockState state, Block bookshelves) {
		return FabricBlockTags.BOOKSHELVES.contains(state.getBlock());
	}
}
