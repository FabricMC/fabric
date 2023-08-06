/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
