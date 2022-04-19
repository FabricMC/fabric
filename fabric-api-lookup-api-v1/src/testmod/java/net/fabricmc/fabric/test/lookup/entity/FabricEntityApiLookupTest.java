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

package net.fabricmc.fabric.test.lookup.entity;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.test.lookup.FabricApiLookupTest;
import net.fabricmc.fabric.test.lookup.api.Inspectable;

public class FabricEntityApiLookupTest {
	public static final EntityApiLookup<Inspectable, Void> INSPECTABLE =
			EntityApiLookup.get(new Identifier(FabricApiLookupTest.MOD_ID, "inspectable"), Inspectable.class, Void.class);

	public static final EntityType<InspectablePigEntity> INSPECTABLE_PIG = FabricEntityTypeBuilder.create()
			.spawnGroup(SpawnGroup.CREATURE)
			.entityFactory(InspectablePigEntity::new)
			.dimensions(EntityDimensions.changing(0.9F, 0.9F))
			.trackRangeChunks(10)
			.build();

	public static void onInitialize() {
		Registry.register(Registry.ENTITY_TYPE, new Identifier(FabricApiLookupTest.MOD_ID, "inspectable_pig"), INSPECTABLE_PIG);
		FabricDefaultAttributeRegistry.register(INSPECTABLE_PIG, PigEntity.createPigAttributes());

		INSPECTABLE.registerSelf(INSPECTABLE_PIG);
		INSPECTABLE.registerForTypes(
				(entity, context) -> () -> new LiteralText("registerForTypes: " + entity.getClass().getName()),
				EntityType.PLAYER,
				EntityType.COW);
		INSPECTABLE.registerFallback((entity, context) -> {
			if (entity instanceof CreeperEntity) {
				return () -> new LiteralText("registerFallback: CreeperEntity");
			} else {
				return null;
			}
		});
	}
}
