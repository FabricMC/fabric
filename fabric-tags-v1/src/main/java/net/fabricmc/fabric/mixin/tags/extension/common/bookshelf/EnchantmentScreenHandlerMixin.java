package net.fabricmc.fabric.mixin.tags.extension.common.bookshelf;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.screen.EnchantmentScreenHandler;

import net.fabricmc.fabric.api.tags.v1.FabricBlockTags;

@Mixin(EnchantmentScreenHandler.class)
public abstract class EnchantmentScreenHandlerMixin {
	@Redirect(
			method = "method_17411", // Lambda reference
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"
			),
			expect = 6
	)
	private boolean onContentChanged$isOf(BlockState state, Block bookshelf) {
		return FabricBlockTags.BOOKSHELVES.contains(state.getBlock());
	}
}
