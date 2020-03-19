package net.fabricmc.fabric.mixin.event.interaction;

import net.fabricmc.fabric.impl.event.interaction.EntityHealthChangeCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
	/**
	 * Callback injector for monitoring entity health changes.
	 */
	@Inject(at = @At("INVOKE"), method = "setHealth(F)V", cancellable = true)
	private void entityHealthChange(float health, CallbackInfo ci) {
		ActionResult result = EntityHealthChangeCallback.EVENT.invoker().health(((LivingEntity) (Object) this), health);
		if (result == ActionResult.FAIL) {
			ci.cancel();
		}
	}
}
