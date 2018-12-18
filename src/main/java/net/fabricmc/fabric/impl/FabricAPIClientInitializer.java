/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.impl;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.networking.CustomPayloadPacketRegistry;
import net.fabricmc.fabric.registry.RegistrySyncManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServer;

public class FabricAPIClientInitializer implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		CustomPayloadPacketRegistry.CLIENT.register(RegistrySyncManager.ID, (ctx, buf) -> {
			// if not hosting server, apply packet
			RegistrySyncManager.receivePacket(ctx, buf, !MinecraftClient.getInstance().method_1496());
		});
	}
}
