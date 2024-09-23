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

package net.fabricmc.fabric.test.components;

import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.server.world.ServerWorld;

import net.fabricmc.fabric.api.components.v1.api.Component;

public record CreeperColor(int r, int g, int b) implements Component<CreeperEntity> {
	public CreeperColor() {
		this(9, 166, 3);
	}

	void onLoad(CreeperEntity creeper, ServerWorld world) {
		ComponentTestMod.LOGGER.info("Creeper with color {} loaded at {}", this, creeper.getBlockPos());
	}

	void onUnload(CreeperEntity creeper) {
		ComponentTestMod.LOGGER.info("Creeper with color {} unloaded at {}", this, creeper.getBlockPos());
	}
}
