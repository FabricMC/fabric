package net.fabricmc.fabric.test.client.event.interaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.Items;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;

public class ClientPreAttackTests implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientPreAttackTests.class);

	@Override
	public void onInitializeClient() {
		ClientPreAttackCallback.EVENT.register(player -> {
			if (player.getMainHandStack().getItem() == Items.TORCH) {
				KeyBinding attackKey = MinecraftClient.getInstance().options.attackKey;
				LOGGER.info("Attacking using torch intercepted. Attack key clicks: {}", attackKey.wasPressed());
				return true;
			}

			return false;
		});
	}
}
