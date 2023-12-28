package net.fabricmc.fabric.mixin.attachment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;

@Mixin(ServerPlayerEntity.class)
abstract class ServerPlayerEntityMixin implements AttachmentTargetImpl {
	@Inject(method = "copyFrom", at = @At("TAIL"))
	private void copyAttachmentsOnRespawn(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
		AttachmentTargetImpl.copyOnRespawn((AttachmentTargetImpl) oldPlayer, this, alive);
	}
}
