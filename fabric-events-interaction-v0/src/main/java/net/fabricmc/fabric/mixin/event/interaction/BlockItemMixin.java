package net.fabricmc.fabric.mixin.event.interaction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

import net.fabricmc.fabric.api.event.player.PlayerPlaceBlockEvents;

@Mixin(BlockItem.class)
public class BlockItemMixin {
	@Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
	private void beforeBlockPlace(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
		if(PlayerPlaceBlockEvents.ALLOW.invoker().allowBlockPlace(context.getPlayer())) {
			ActionResult result = PlayerPlaceBlockEvents.BEFORE.invoker().beforeBlockPlace(new ItemPlacementContext(context));
			if (result != ActionResult.PASS) {
				PlayerPlaceBlockEvents.CANCELLED.invoker().cancelledBlockPlace(context.getWorld(), context.getPlayer(), context.getBlockPos(), context.getWorld().getBlockState(context.getBlockPos()));

				cir.setReturnValue(result);
			}
			return;
		}
		cir.setReturnValue(ActionResult.FAIL);
	}
}
