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

package net.fabricmc.fabric.test.server.consent.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.server.consent.v1.client.ClientFabricServerConsentEvents;

public class ClientServerConsentTest implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientFabricServerConsentEvents.MODS_SENT.register((client, handler, buf, responseSender) -> {
			List<String> illegalMods = buf.readCollection(ArrayList::new, PacketByteBuf::readString);
			client.player.sendMessage(Text.of("Illegal mods: " + illegalMods.toString()));
		});
		ClientFabricServerConsentEvents.FEATURES_SENT.register((client, handler, buf, responseSender) -> {
			List<String> illegalFeatures = buf.readCollection(ArrayList::new, PacketByteBuf::readString);
			client.player.sendMessage(Text.of("Illegal features: " + illegalFeatures.toString()));
		});
	}
}
