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

package net.fabricmc.fabric.test.object.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.world.Heightmap;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;

public class FabricEntityTypeTest {
	@BeforeAll
	static void beforeAll() {
		SharedConstants.createGameVersion();
		Bootstrap.initialize();
	}

	@Test
	void buildEntityType() {
		EntityType<Entity> type = EntityType.Builder.create(SpawnGroup.MISC)
				.alwaysUpdateVelocity(true)
				.build();

		assertNotNull(type);
		assertTrue(type.alwaysUpdateVelocity());
	}

	@Test
	void buildLivingEntityType() {
		EntityType<LivingEntity> type = FabricEntityType.Builder.createLiving((t, w) -> null, SpawnGroup.MISC, living -> living
						.defaultAttributes(FabricEntityTypeTest::createAttributes)
		).build();

		assertNotNull(type);
		assertNotNull(DefaultAttributeRegistry.get(type));
	}

	@Test
	void buildMobEntityType() {
		EntityType<MobEntity> type = FabricEntityType.Builder.createMob((t, w) -> null, SpawnGroup.MISC, mob -> mob
				.spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, PigEntity::canMobSpawn)
				.defaultAttributes(FabricEntityTypeTest::createAttributes)
		).build();

		assertNotNull(type);
		assertEquals(SpawnRestriction.Location.ON_GROUND, SpawnRestriction.getLocation(type));
		assertNotNull(DefaultAttributeRegistry.get(type));
	}

	private static DefaultAttributeContainer.Builder createAttributes() {
		return MobEntity.createMobAttributes()
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25);
	}
}
