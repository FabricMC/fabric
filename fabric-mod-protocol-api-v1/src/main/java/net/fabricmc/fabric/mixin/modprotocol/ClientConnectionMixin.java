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

package net.fabricmc.fabric.mixin.modprotocol;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.network.ClientConnection;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.modprotocol.RemoteProtocolStorage;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin implements RemoteProtocolStorage {
	@Unique
	@Nullable
	private Object2IntMap<Identifier> remoteProtocol = null;

	@Override
	public Object2IntMap<Identifier> fabric$getRemoteProtocol() {
		return this.remoteProtocol;
	}

	@Override
	public void fabric$setRemoteProtocol(Object2IntMap<Identifier> protocol) {
		this.remoteProtocol = protocol;
	}
}
