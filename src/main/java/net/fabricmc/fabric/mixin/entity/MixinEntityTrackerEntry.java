package net.fabricmc.fabric.mixin.entity;

import net.fabricmc.fabric.entity.EntityTrackingRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.server.network.EntityTrackerEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityTrackerEntry.class)
public class MixinEntityTrackerEntry {
	@Shadow
	private Entity entity;

	@Inject(at = @At(value = "CONSTANT", args = {"stringValue=Don't know how to add "}), method = "createSpawnPacket", cancellable = true)
	public void createSpawnPacket(CallbackInfoReturnable<Packet> info) {
		Packet packet = EntityTrackingRegistry.INSTANCE.createSpawnPacket(entity);
		if (packet != null) {
			info.setReturnValue(packet);
			info.cancel();
		}
	}
}
