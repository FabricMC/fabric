package net.fabricmc.fabric.impl.registry.sync;

import java.util.function.Consumer;

import com.mojang.authlib.GameProfile;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;

public record ConfiguringServerPlayer(GameProfile gameProfile, Consumer<Packet<?>> sender) {
    public void sendPacket(Identifier identifier, PacketByteBuf buf) {
        sender.accept(ServerConfigurationNetworking.createS2CPacket(identifier, buf));
    }
}
