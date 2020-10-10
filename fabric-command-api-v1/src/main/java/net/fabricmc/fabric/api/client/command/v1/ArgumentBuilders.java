package net.fabricmc.fabric.api.client.command.v1;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

/**
 * Provides helper methods for constructing argument builders
 * for client-sided commands.
 *
 * <p>This is an alternative to the server-side helper methods in
 * {@link net.minecraft.server.command.CommandManager}.
 */
public final class ArgumentBuilders {
	private ArgumentBuilders() {
	}

	/**
	 * Creates a literal argument builder.
	 *
	 * @param name the literal name
	 * @return the created argument builder
	 */
	public static LiteralArgumentBuilder<FabricClientCommandSource> literal(String name) {
		return LiteralArgumentBuilder.literal(name);
	}

	/**
	 * Creates a required argument builder.
	 *
	 * @param name the name of the argument
	 * @param type the type of the argument
	 * @param <T>  the type of the parsed argument value
	 * @return the created argument builder
	 */
	public static <T> RequiredArgumentBuilder<FabricClientCommandSource, T> argument(String name, ArgumentType<T> type) {
		return RequiredArgumentBuilder.argument(name, type);
	}
}
