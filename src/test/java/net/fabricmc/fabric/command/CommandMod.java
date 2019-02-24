package net.fabricmc.fabric.command;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.CommandRegistry;
import net.fabricmc.fabric.api.command.CommandType;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.text.StringTextComponent;

public class CommandMod implements ModInitializer {
	@Override
	public void onInitialize() {
		testCommand(CommandType.DEFAULT);
		testCommand(CommandType.INTEGRATED);
		testCommand(CommandType.DEDICATED);
	}

	private static void testCommand(CommandType type){
		CommandRegistry.INSTANCE.register(type, serverCommandSourceCommandDispatcher ->
			serverCommandSourceCommandDispatcher.register(ServerCommandManager
				.literal(type.name())
				.executes(context -> {
					context.getSource().sendFeedback(new StringTextComponent("hello " + type.name()), false);
					return 1;
				})));
	}
}
