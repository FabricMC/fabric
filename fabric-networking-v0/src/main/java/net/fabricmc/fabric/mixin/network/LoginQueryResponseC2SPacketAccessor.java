package net.fabricmc.fabric.mixin.network;

import net.minecraft.client.network.packet.LoginQueryRequestS2CPacket;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LoginQueryResponseC2SPacket.class)
public interface LoginQueryResponseC2SPacketAccessor {
	@Accessor
	int getQueryId();
	@Accessor
	PacketByteBuf getResponse();
}
