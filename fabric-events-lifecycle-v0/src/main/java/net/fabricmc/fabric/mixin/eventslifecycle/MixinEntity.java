package net.fabricmc.fabric.mixin.eventslifecycle;


import net.fabricmc.fabric.api.event.entity.EntityTickCallback;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity {

	@Inject(method = "tick", at = @At("TAIL"))
	public void tickEntity(CallbackInfo ci) {
		EntityTickCallback.event(((Entity)(Object)this).getType()).tick((Entity) (Object) this);
	}
}
