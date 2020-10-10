package net.fabricmc.fabric.mixin.command.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

@Mixin(ClientCommandSource.class)
abstract class ClientCommandSourceMixin implements FabricClientCommandSource {
	@Shadow
	@Final
	private MinecraftClient client;

	@Override
	public void sendFeedback(Text message) {
		client.inGameHud.addChatMessage(MessageType.SYSTEM, message, Util.NIL_UUID);
	}

	@Override
	public void sendError(Text message) {
		client.inGameHud.addChatMessage(MessageType.SYSTEM, message.copy().formatted(Formatting.RED), Util.NIL_UUID);
	}

	@Override
	public MinecraftClient getClient() {
		return client;
	}

	@Override
	public ClientPlayerEntity getPlayer() {
		return client.player;
	}

	@Override
	public ClientWorld getWorld() {
		return client.world;
	}
}
