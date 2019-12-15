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

package net.fabricmc.fabric.impl.networking;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.networking.v1.receiver.ClientPacketReceiverRegistries;
import net.fabricmc.fabric.impl.networking.receiver.ClientPacketReceivers;

public final class NetworkingClientInitializer implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPacketReceiverRegistries.LOGIN_QUERY.register(PacketHelper.QUERY_CHANNELS, (context, buffer) -> {
			NetworkingInitializer.getInstance().getConnectionSpecificChannels().put(context.getNetworkHandler().getConnection(), NetworkingInitializer.readChannels(buffer));
			context.sendResponse(NetworkingInitializer.writeChannels(ClientPacketReceivers.PLAY.getAcceptedChannels()));
		});
	}
}
