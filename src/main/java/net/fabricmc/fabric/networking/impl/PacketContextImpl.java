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

package net.fabricmc.fabric.networking.impl;

import net.fabricmc.api.Side;
import net.fabricmc.fabric.networking.PacketContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ThreadTaskQueue;

public class PacketContextImpl implements PacketContext {
	private final Side side;
	private final EntityPlayer player;
	private final ThreadTaskQueue taskQueue;

	public PacketContextImpl(Side side, EntityPlayer player, ThreadTaskQueue taskQueue) {
		this.side = side;
		this.player = player;
		this.taskQueue = taskQueue;
	}

	@Override
	public Side getSide() {
		return side;
	}

	@Override
	public EntityPlayer getPlayer() {
		return player;
	}

	@Override
	public ThreadTaskQueue getTaskQueue() {
		return taskQueue;
	}
}
