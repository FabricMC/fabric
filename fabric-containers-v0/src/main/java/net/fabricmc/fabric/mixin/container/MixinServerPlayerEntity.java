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

package net.fabricmc.fabric.mixin.container;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.impl.container.ServerPlayerEntitySyncHook;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity implements ServerPlayerEntitySyncHook {
	@Shadow
	private int containerSyncId;

	@Shadow
	protected abstract void incrementContainerSyncId();

	@Override
	public int fabric_incrementSyncId() {
		incrementContainerSyncId();
		return containerSyncId;
	}
}
