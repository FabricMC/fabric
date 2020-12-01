package net.fabricmc.fabric.api.command.v1;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.fabric.impl.command.v1.ClientCommandManagerImpl;
import net.minecraft.command.CommandSource;

public interface ClientCommandManager {
    ClientCommandManager INSTANCE = new ClientCommandManagerImpl();

    static LiteralArgumentBuilder<CommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    static <T> RequiredArgumentBuilder<CommandSource, T> argument(String name, ArgumentType<T> argumentType) {
        return RequiredArgumentBuilder.argument(name, argumentType);
    }

    CommandDispatcher<CommandSource> getDispatcher();

    default <T> int execute(CommandDispatcher<T> dispatcher, String command, T source) {
        return execute(dispatcher, new StringReader(command), source);
    }

    default <T> int execute(CommandDispatcher<T> dispatcher, StringReader command, T source) {
        if (command.canRead(1) && command.peek() == '/') {
            command.skip();
        }

        return execute(dispatcher, dispatcher.parse(command, source));
    }

    <T> int execute(CommandDispatcher<T> dispatcher, ParseResults<T> parseResults);
}
