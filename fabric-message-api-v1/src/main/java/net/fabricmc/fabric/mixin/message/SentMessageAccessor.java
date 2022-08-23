package net.fabricmc.fabric.mixin.message;

import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SentMessage.Chat.class)
public interface SentMessageAccessor {
	@Accessor
	SignedMessage getMessage();
}
