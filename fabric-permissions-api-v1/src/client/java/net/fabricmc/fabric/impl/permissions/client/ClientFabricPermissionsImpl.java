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

package net.fabricmc.fabric.impl.permissions.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import net.minecraft.network.PacketByteBuf;

import java.util.Collection;

import static net.fabricmc.fabric.impl.permissions.FabricPermissionsImpl.PERMISSIONS_CHANNEL;

public final class ClientFabricPermissionsImpl implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			Collection<ModContainer> mods = FabricLoader.getInstance().getAllMods();
			PacketByteBuf buf = PacketByteBufs.create();
			buf.writeCollection(mods, (buff, container) -> buff.writeString(container.getMetadata().getId()));
			sender.sendPacket(PERMISSIONS_CHANNEL, buf);
		});

		ClientPlayNetworking.registerGlobalReceiver(PERMISSIONS_CHANNEL, new ClientModListHandler());
	}
}
