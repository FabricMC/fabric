package net.fabric.test;

import com.mojang.brigadier.Command;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.LiteralText;

public class FabricCommandsTest implements ModInitializer {

	@Override
	public void onInitialize() {
		CommandRegistry.INSTANCE.register(false, (dispatcher) -> dispatcher.register(CommandManager.literal("fabric_test").executes(c -> {
			c.getSource().sendFeedback(new LiteralText("Command works!"), false);
			return Command.SINGLE_SUCCESS;
		})));

		CommandRegistry.INSTANCE.register(true, (dispatcher) -> dispatcher.register(CommandManager.literal("fabric_test_dedicated").executes(c -> {
			c.getSource().sendFeedback(new LiteralText("Command works!"), false);
			return Command.SINGLE_SUCCESS;
		})));
	}
}
