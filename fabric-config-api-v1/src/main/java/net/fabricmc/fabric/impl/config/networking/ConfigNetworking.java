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

package net.fabricmc.fabric.impl.config.networking;

import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.config.v1.FabricSaveTypes;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.config.ConfigValueSender;
import net.fabricmc.loader.api.config.ConfigDefinition;
import net.fabricmc.loader.api.config.ConfigManager;
import net.fabricmc.loader.api.config.value.ValueContainer;
import net.fabricmc.loader.config.ValueContainerProviders;

public class ConfigNetworking implements ModInitializer, ClientModInitializer {
	public static final Identifier SYNC_CONFIG = new Identifier("fabric", "packet/sync_values");
	public static final Identifier USER_CONFIG = new Identifier("fabric", "packet/sync_values/user");

	@Override
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		ClientPlayConnectionEvents.JOIN.register(ConfigSenders::sendConfigValues);
		ClientPlayConnectionEvents.INIT.register((handler, client) -> {
			ClientPlayNetworking.registerReceiver(SYNC_CONFIG, ConfigReceivers::receiveLevelConfigValues);
			ClientPlayNetworking.registerReceiver(USER_CONFIG, ConfigReceivers::receiveUserConfigValues);
		});
	}

	@Override
	public void onInitialize() {
		ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
			ServerPlayNetworking.registerReceiver(handler, SYNC_CONFIG, ConfigReceivers::receiveConfigValues);
			((ConfigValueSender) server).sendCached(handler.player);

			ValueContainer valueContainer = ValueContainerProviders.getInstance(FabricSaveTypes.LEVEL).getValueContainer();

			for (ConfigDefinition<?> configDefinition : ConfigManager.getConfigKeys()) {
				ConfigSenders.sendToPlayer(configDefinition, handler.player, valueContainer);
			}
		}));

		ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) ->
				((ConfigValueSender) server).drop(handler.player)));
	}
}
