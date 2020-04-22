package net.fabricmc.fabric.mixin.tool.attribute;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.fabric.impl.tool.attribute.ToolManager;

@Mixin(PlayerInventory.class)
public abstract class MixinPlayerInventory {
	@Shadow
	public abstract ItemStack getInvStack(int slot);

	@Shadow
	public int selectedSlot;

	@Inject(at = @At("HEAD"), method = "isUsingEffectiveTool", cancellable = true)
	public void isEffectiveOn(BlockState state, CallbackInfoReturnable<Boolean> info) {
		TriState triState = ToolManager.handleIsEffectiveOn(getInvStack(selectedSlot), state, null);

		if (triState != TriState.DEFAULT) {
			info.setReturnValue(triState.get());
			info.cancel();
		}
	}
}
