package net.fabricmc.mixin.event.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.dimension.DimensionType;

import net.fabricmc.api.event.player.v1.PlayerRespawnCallback;

@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager {
	/**
	 * The reason why this injection was chosen rather than injecting at the "TAIL" of copyFrom is to allow repositioning of the player before respawn.
	 */
	@Inject(at = @At("TAIL"), method = "respawnPlayer(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/dimension/DimensionType;Z)Lnet/minecraft/server/network/ServerPlayerEntity;")
	private void onPlayerRespawn(ServerPlayerEntity oldPlayer, DimensionType newDimension, boolean oldPlayerIsAlive, CallbackInfoReturnable<ServerPlayerEntity> cir) {
		ServerPlayerEntity newPlayer = cir.getReturnValue();
		PlayerRespawnCallback.EVENT.invoker().onRespawn(newPlayer, oldPlayer, newDimension, oldPlayerIsAlive);
	}
}
