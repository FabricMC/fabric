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

package net.fabricmc.fabric.api.networking.v1.client;

import net.minecraft.client.MinecraftClient;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ListenerContext;
import net.fabricmc.fabric.api.networking.v1.PacketReceiver;

/**
 * Represents a context for {@linkplain PacketReceiver packet reception}
 * on the logical client.
 *
 * <p>Compared to the basic listener context, the client context offers
 * access to the active {@linkplain MinecraftClient <i>Minecraft</i> Client}.</p>
 */
@Environment(EnvType.CLIENT)
public interface ClientContext extends ListenerContext {
	/**
	 * {@inheritDoc}
	 *
	 * <p>In a client context, the game engine is always a <i>Minecraft</i> Client.</p>
	 *
	 * @return the <i>Minecraft</i> Client
	 */
	@Override
	MinecraftClient getEngine();
}
