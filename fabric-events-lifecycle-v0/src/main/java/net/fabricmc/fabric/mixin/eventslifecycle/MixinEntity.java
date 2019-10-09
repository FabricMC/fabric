package net.fabricmc.fabric.mixin.eventslifecycle;


import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.entity.EntityTickCallback;
import net.fabricmc.fabric.impl.event.EntityEventInternals;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity {
	@Unique
	private Event<EntityTickCallback<Entity>> tickEvent = EntityEventInternals.getOrCreateEntityEvent(this.getClass());

	@Shadow public abstract EntityType<?> getType();

	@Inject(method = "tick", at = @At("TAIL"))
	private void tickEntity(CallbackInfo ci) {
		this.tickEvent.invoker().tick((Entity)(Object)this);
	}

}
