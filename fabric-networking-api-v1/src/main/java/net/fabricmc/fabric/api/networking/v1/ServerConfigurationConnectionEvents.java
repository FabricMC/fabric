package net.fabricmc.fabric.api.networking.v1;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.server.network.ServerPlayerConfigurationTask;

import java.util.function.Consumer;

public class ServerConfigurationConnectionEvents {
    public static final Event<Send> SEND_CONFIGURATION = EventFactory.createArrayBacked(Send.class, callbacks -> (handler, server, taskConsumer) -> {
        for (Send callback : callbacks) {
            callback.onSendConfiguration(handler, server, taskConsumer);
        }
    });

    public static final Event<ServerConfigurationConnectionEvents.Disconnect> DISCONNECT = EventFactory.createArrayBacked(ServerConfigurationConnectionEvents.Disconnect.class, callbacks -> (handler, server) -> {
        for (ServerConfigurationConnectionEvents.Disconnect callback : callbacks) {
            callback.onConfigureDisconnect(handler, server);
        }
    });

    @FunctionalInterface
    public interface Send {
        // TODO is having the task consumer like this a good idea?
        void onSendConfiguration(ServerConfigurationNetworkHandler handler, MinecraftServer server, Consumer<ServerPlayerConfigurationTask> taskConsumer);
    }

    @FunctionalInterface
    public interface Disconnect {
        void onConfigureDisconnect(ServerConfigurationNetworkHandler handler, MinecraftServer server);
    }
}
