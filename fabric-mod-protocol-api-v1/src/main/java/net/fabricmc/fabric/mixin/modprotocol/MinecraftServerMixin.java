package net.fabricmc.fabric.mixin.modprotocol;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.fabricmc.fabric.impl.modprotocol.ModProtocolManager;
import net.fabricmc.fabric.impl.modprotocol.ServerMetadataExtension;

import net.minecraft.server.MinecraftServer;

import net.minecraft.server.ServerMetadata;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@ModifyReturnValue(method = "createMetadata", at = @At("RETURN"))
	private ServerMetadata addModProtocol(ServerMetadata original) {
		if (!ModProtocolManager.CLIENT_REQUIRED.isEmpty()) {
			ServerMetadataExtension.of(original).fabric$setModProtocol(ModProtocolManager.CLIENT_REQUIRED);
		}
		return original;
	}
}
