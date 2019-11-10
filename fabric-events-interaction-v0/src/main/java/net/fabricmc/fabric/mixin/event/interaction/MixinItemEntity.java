package net.fabricmc.fabric.mixin.event.interaction;

import net.fabricmc.fabric.api.event.player.PlayerPickupItemCallback;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity {
	@Inject(at = @At("HEAD"), method = "onPlayerCollision", cancellable = true)
	private void pickupItem(final PlayerEntity playerEntity, final CallbackInfo info) {
		ActionResult result = PlayerPickupItemCallback.EVENT.invoker().interact(playerEntity, (ItemEntity) (Object) this);
		if (result == ActionResult.FAIL) {
			info.cancel();
		}
	}
}
