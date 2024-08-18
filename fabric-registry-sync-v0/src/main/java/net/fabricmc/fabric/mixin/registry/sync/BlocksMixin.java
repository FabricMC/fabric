package net.fabricmc.fabric.mixin.registry.sync;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;

@Mixin(Blocks.class)
public class BlocksMixin {
	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void initShapeCache(CallbackInfo ci) {
		// Ensure that any blocks added after this point have their shape cache initialized.
		RegistryEntryAddedCallback.event(Registries.BLOCK).register((rawId, id, block) -> {
			for (BlockState state : block.getStateManager().getStates()) {
				state.initShapeCache();
			}
		});
	}
}
