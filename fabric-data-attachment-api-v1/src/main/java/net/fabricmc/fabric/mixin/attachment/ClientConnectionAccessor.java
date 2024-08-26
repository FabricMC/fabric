package net.fabricmc.fabric.mixin.attachment;

import io.netty.channel.Channel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.ClientConnection;

@Mixin(ClientConnection.class)
public interface ClientConnectionAccessor {
	@Accessor
	Channel getChannel();
}
