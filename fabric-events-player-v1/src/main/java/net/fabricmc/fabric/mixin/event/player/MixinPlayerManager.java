package net.fabricmc.fabric.mixin.event.player;

import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.dimension.DimensionType;

import net.fabricmc.fabric.api.event.player.v1.PlayerRespawnCallback;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager {
	/**
	 * This injection point has been chosen to allow repositioning of the player by dimension and location before respawn packets are sent to the client.
	 */
	@Inject(method = "respawnPlayer(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/dimension/DimensionType;Z)Lnet/minecraft/server/network/ServerPlayerEntity;",
		slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;doesNotCollide(Lnet/minecraft/entity/Entity;)Z")),
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/World;getLevelProperties()Lnet/minecraft/world/level/LevelProperties;"
		),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void fabric_onPlayerRespawnFireEvent(ServerPlayerEntity oldPlayer, DimensionType newDimension, boolean oldPlayerIsAlive, CallbackInfoReturnable<ServerPlayerEntity> cir, BlockPos spawnPos, boolean forcedSpawn,
		ServerPlayerInteractionManager manager, ServerPlayerEntity clone) {
		PlayerRespawnCallback.EVENT.invoker().onRespawn(clone, oldPlayer, newDimension, oldPlayerIsAlive);
	}
}
