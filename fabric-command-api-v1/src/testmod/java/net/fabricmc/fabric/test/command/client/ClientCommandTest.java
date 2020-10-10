package net.fabricmc.fabric.test.command.client;

import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import net.minecraft.text.LiteralText;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ArgumentBuilders;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandRegistrationCallback;

public final class ClientCommandTest implements ClientModInitializer {
	private static final DynamicCommandExceptionType IS_NULL = new DynamicCommandExceptionType(x -> new LiteralText("The " + x + " is null"));

	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.event().register(registerSimpleCommands("This is a client command"));
		// Using `\` as prefix
		ClientCommandRegistrationCallback.event('\\').register(registerSimpleCommands("This is a client command with backslashes"));
	}

	private static ClientCommandRegistrationCallback registerSimpleCommands(String message) {
		return dispatcher -> {
			dispatcher.register(ArgumentBuilders.literal("test-client-cmd").executes(context -> {
				context.getSource().sendFeedback(new LiteralText(message));

				if (context.getSource().getClient() == null) {
					throw IS_NULL.create("client");
				}

				if (context.getSource().getWorld() == null) {
					throw IS_NULL.create("world");
				}

				if (context.getSource().getPlayer() == null) {
					throw IS_NULL.create("player");
				}

				return 0;
			}));
		};
	}
}
