package net.fabricmc.fabric.mixin.network;

import net.minecraft.client.network.packet.LoginQueryRequestS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LoginQueryRequestS2CPacket.class)
public interface LoginQueryRequestS2CPacketAccessor {
	@Accessor
	int getQueryId();
	@Accessor
	Identifier getChannel();
	@Accessor
	PacketByteBuf getPayload();
	@Accessor
	void setQueryId(int queryId);
	@Accessor
	void setChannel(Identifier channel);
	@Accessor
	void setPayload(PacketByteBuf payload);
}
