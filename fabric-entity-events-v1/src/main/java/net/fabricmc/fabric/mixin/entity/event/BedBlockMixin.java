package net.fabricmc.fabric.mixin.entity.event;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BedBlock;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(BedBlock.class)
abstract class BedBlockMixin {
	// Synthetic lambda body for Either.ifLeft in onUse
	@Inject(method = "method_19283", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;sendMessage(Lnet/minecraft/text/Text;Z)V"), cancellable = true)
	private static void onOnUse(PlayerEntity player, PlayerEntity.SleepFailureReason reason, CallbackInfo info) {
		// EntitySleepEvents.ALLOW_SLEEPING allows modders to return SleepFailureReason instances
		// with a null message, which vanilla's code doesn't guard against. This prevents a (luckily caught) NPE
		// when a failure reason like that is returned from the event.
		if (reason.toText() == null) {
			info.cancel();
		}
	}
}
