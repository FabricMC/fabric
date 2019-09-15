package net.fabricmc.fabric.mixin.tools;

import net.fabricmc.fabric.api.tools.v1.ActableAttributeHolder;
import net.fabricmc.fabric.api.tools.v1.ToolActor;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.fabric.impl.tools.ToolManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class MixinPlayerInventory {
	private final ToolActor actor = ToolActor.of(this.player);

	@Shadow
	@Final
	public DefaultedList<ItemStack> main;

	@Shadow
	public int selectedSlot;

	@Shadow
	@Final
	public PlayerEntity player;

	@Shadow
	public abstract ItemStack getInvStack(int int_1);

	@Inject(method = "isUsingEffectiveTool", at = @At("HEAD"))
	public void actMiningLevel(BlockState state, CallbackInfoReturnable<Boolean> info) {
		ItemStack stack = this.getInvStack(this.selectedSlot);

		if (stack.getItem() instanceof ActableAttributeHolder) {
			TriState ret = ToolManager.handleIsEffectiveOn(stack, state, actor);

			if (ret != TriState.DEFAULT) {
				info.setReturnValue(ret.get());
			}

		}

	}

	@Inject(method = "getBlockBreakingSpeed", at = @At("HEAD"))
	public void actMiningSleed(BlockState state, CallbackInfoReturnable<Float> info) {
		ItemStack stack = this.main.get(this.selectedSlot);

		if (stack.getItem() instanceof ActableAttributeHolder) {
			info.setReturnValue(((ActableAttributeHolder)stack.getItem()).getMiningSpeed(stack, actor));
		}

	}
}
