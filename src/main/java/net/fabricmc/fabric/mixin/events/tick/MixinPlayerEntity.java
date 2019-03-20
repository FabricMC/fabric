package net.fabricmc.fabric.mixin.events.tick;

import net.fabricmc.fabric.api.event.player.PlayerTickCallback;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {
	protected MixinPlayerEntity(EntityType<? extends LivingEntity> type, World world) {
		super(type, world);
	}

	@Inject(method = "update", at = @At("TAIL"))
	public void tickEquipmentSlots(CallbackInfo ci) {
		PlayerEntity player = (PlayerEntity)(Object)this;
		PlayerTickCallback.EVENT.invoker().tick(player);
	}
}
