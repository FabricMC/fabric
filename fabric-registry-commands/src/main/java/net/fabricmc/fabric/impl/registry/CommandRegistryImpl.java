package net.fabricmc.fabric.impl.registry;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class CommandRegistryImpl {
    public static final CommandRegistryImpl INSTANCE = new CommandRegistryImpl();

    private final List<Consumer<CommandDispatcher<ServerCommandSource>>> serverCommands;
    private final List<Consumer<CommandDispatcher<ServerCommandSource>>> dedicatedServerCommands;

    public CommandRegistryImpl() {
        this.serverCommands = new ArrayList<>();
        this.dedicatedServerCommands = new ArrayList<>();
    }

    public List<Consumer<CommandDispatcher<ServerCommandSource>>> entries(boolean dedicated) {
        return Collections.unmodifiableList(dedicated ? dedicatedServerCommands : serverCommands);
    }

    public void register(boolean dedicated, Consumer<CommandDispatcher<ServerCommandSource>> consumer) {
        if (dedicated) {
            dedicatedServerCommands.add(consumer);
        } else {
            serverCommands.add(consumer);
        }
    }
}
