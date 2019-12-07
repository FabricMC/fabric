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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.thread.ThreadExecutor;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.networking.v1.receiver.PlayPacketContext;
import net.fabricmc.fabric.api.networking.v1.receiver.ServerPacketContext;

final class WrappedPacketContext implements PacketContext {
	private final PlayPacketContext parent;

	WrappedPacketContext(PlayPacketContext parent) {
		this.parent = parent;
	}

	@Override
	public EnvType getPacketEnvironment() {
		return parent instanceof ServerPacketContext ? EnvType.SERVER : EnvType.CLIENT;
	}

	@Override
	public PlayerEntity getPlayer() {
		return parent.getPlayer();
	}

	@Override
	public ThreadExecutor getTaskQueue() {
		return parent.getEngine();
	}
}
