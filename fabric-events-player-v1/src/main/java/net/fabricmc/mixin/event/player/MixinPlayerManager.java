package net.fabricmc.mixin.event.player;

import net.fabricmc.api.event.v1.player.PlayerRespawnCallback;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager {
	@Inject(at = @At("TAIL"), method = "respawnPlayer(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/dimension/DimensionType;Z)Lnet/minecraft/server/network/ServerPlayerEntity;")
	private void onPlayerRespawn(ServerPlayerEntity oldPlayer, DimensionType newDimension, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir) {
		ServerPlayerEntity newPlayer = cir.getReturnValue();
		PlayerRespawnCallback.EVENT.invoker().onRespawn(newPlayer, oldPlayer, newDimension, alive);
	}
}
