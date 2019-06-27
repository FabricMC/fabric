package net.fabricmc.fabric.mixin.eventsinteraction;

import net.fabricmc.fabric.api.event.player.PickupItemCallback;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity
{
	@Shadow public abstract ItemStack getStack();

	@Inject(at = @At("HEAD"), method = "onPlayerCollision", cancellable = true)
	private void pickupItem(final PlayerEntity playerEntity, final CallbackInfo info) {
		ActionResult result = PickupItemCallback.EVENT.invoker().interact(playerEntity, (ItemEntity) (Object) this);
		if(result == ActionResult.FAIL) {
			info.cancel();
		}
	}
}
