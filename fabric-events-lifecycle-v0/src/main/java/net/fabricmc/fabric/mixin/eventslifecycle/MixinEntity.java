package net.fabricmc.fabric.mixin.eventslifecycle;


import net.fabricmc.fabric.api.event.entity.EntityTickCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity {

	@Shadow public abstract EntityType<?> getType();

	@Inject(method = "tick", at = @At("TAIL"))
	public void tickEntity(CallbackInfo ci) {
		Entity self = ((Entity)(Object)this);
		((EntityTickCallback) EntityTickCallback.event(self.getType())).tick(self);
	}
}
