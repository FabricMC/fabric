package net.fabricmc.fabric.mixin.idremap;

import net.fabricmc.fabric.impl.dimension.DimensionIdsFixer;
import net.fabricmc.fabric.impl.dimension.FabricDimensionInternals;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager {
    /**
     * Synchronizes raw dimension ids to connecting players
     */
    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/packet/DifficultyS2CPacket;<init>(Lnet/minecraft/world/Difficulty;Z)V"))
    private void onPlayerConnect(ClientConnection conn, ServerPlayerEntity player, CallbackInfo info) {
        // TODO: Refactor out into network + move dimension hook to event
        // No need to send the packet if the player is using the same game instance (dimension types are static)
        if (!player.server.isSinglePlayer() || !conn.isLocal() || FabricDimensionInternals.DEBUG) {
            player.networkHandler.sendPacket(DimensionIdsFixer.createPacket(player.world.getLevelProperties()));
        }
    }
}
