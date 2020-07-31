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

package net.fabricmc.fabric.test.objectbuilder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricSpawnerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.gen.Spawner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ObjectBuilderTest implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitialize() {
		FabricSpawnerRegistry.register(TestSpawner::new);
	}

	private static class TestSpawner implements Spawner {
		@Override
		public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
			int amount = 0;
			for (Entity egg : world.getEntitiesByType(EntityType.EGG, Entity::isAlive)) {
				ChickenEntity chickenEntity = new ChickenEntity(EntityType.CHICKEN, world);
				chickenEntity.refreshPositionAndAngles(egg.getX(), egg.getY(), egg.getZ(), world.random.nextFloat(), world.random.nextFloat());
				if (world.spawnEntity(chickenEntity)) amount++;
			}
			return amount;
		}
	}
}
