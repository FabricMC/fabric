package net.fabricmc.fabric.mixin.mininglevel.client;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.SynchronizeTagsS2CPacket;

import net.fabricmc.fabric.impl.mininglevel.MiningLevelManagerImpl;

@Mixin(ClientPlayNetworkHandler.class)
abstract class ClientPlayNetworkHandlerMixin {
	@Inject(method = "onSynchronizeTags", at = @At("RETURN"), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;tagManager:Lnet/minecraft/tag/TagManager;", opcode = Opcodes.PUTFIELD)))
	private void fabric$clearMiningLevelCache(SynchronizeTagsS2CPacket packet, CallbackInfo info) {
		MiningLevelManagerImpl.clearCache();
	}
}
