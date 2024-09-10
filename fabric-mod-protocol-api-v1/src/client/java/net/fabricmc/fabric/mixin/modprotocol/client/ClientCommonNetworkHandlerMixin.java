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

package net.fabricmc.fabric.mixin.modprotocol.client;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.modprotocol.RemoteProtocolStorage;

@Mixin(ClientCommonNetworkHandler.class)
public class ClientCommonNetworkHandlerMixin implements RemoteProtocolStorage {
	@Shadow
	@Final
	protected ClientConnection connection;

	@Override
	public Object2IntMap<Identifier> fabric$getRemoteProtocol() {
		return ((RemoteProtocolStorage) this.connection).fabric$getRemoteProtocol();
	}

	@Override
	public void fabric$setRemoteProtocol(Object2IntMap<Identifier> protocol) {
		((RemoteProtocolStorage) this.connection).fabric$setRemoteProtocol(protocol);
	}
}
