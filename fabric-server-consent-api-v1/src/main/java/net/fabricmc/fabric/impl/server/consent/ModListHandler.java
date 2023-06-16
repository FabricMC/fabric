/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.impl.server.consent;

import static net.fabricmc.fabric.impl.server.consent.FabricServerConsentImpl.CONSENTS_CHANNEL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Joiner;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.server.consent.v1.FabricServerConsent;

public final class ModListHandler implements ServerPlayNetworking.PlayChannelHandler {
	public static final Map<UUID, List<String>> modsForPlayer = new HashMap<>();

	@Override
	public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		List<String> modIds = buf.readCollection(ArrayList::new, PacketByteBuf::readString);
		modsForPlayer.put(player.getUuid(), new ArrayList<>(modIds));
		modIds.retainAll(FabricServerConsentImpl.illegalMods);

		if (!modIds.isEmpty()) {
			switch (FabricServerConsent.getResponsePolicy()) {
			case WARN -> {
				player.sendMessage(createIllegalModsMessage(modIds));

				PacketByteBuf responseBuf = PacketByteBufs.create();
				responseBuf.writeCollection(FabricServerConsentImpl.illegalFeatures, PacketByteBuf::writeString);
				responseSender.sendPacket(CONSENTS_CHANNEL, responseBuf);
			}
			case KICK -> player.networkHandler.disconnect(createIllegalModsMessage(modIds));
			}
		}
	}

	public static MutableText createIllegalModsMessage(List<String> modIds) {
		if (modIds.size() == 1) {
			return Text.translatable("fabric-server-consent-api-v1.illegalModsWarning.single", modIds.get(0));
		}

		return Text.translatable("fabric-server-consent-api-v1.illegalModsWarning.many", Joiner.on(", ").join(modIds));
	}
}
