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

package net.fabricmc.fabric.test.registry.sync;

import java.util.Map;
import java.util.Objects;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.impl.registry.sync.packet.DirectRegistrySyncPacket;
import net.fabricmc.fabric.impl.registry.sync.packet.NbtRegistrySyncPacket;

@Environment(EnvType.CLIENT)
public class RegistrySyncTestClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(RegistrySyncTest.PACKET_CHECK, (client, handler, buf, responseSender) -> {
			Map<Identifier, Object2IntMap<Identifier>> nbtPacketMap = NbtRegistrySyncPacket.getInstance().readBuffer(buf);
			Map<Identifier, Object2IntMap<Identifier>> directPacketMap = DirectRegistrySyncPacket.getInstance().readBuffer(buf);

			Preconditions.checkArgument(Objects.requireNonNull(nbtPacketMap).equals(directPacketMap), "nbt packet and direct packet are not equal!");
		});
	}
}
