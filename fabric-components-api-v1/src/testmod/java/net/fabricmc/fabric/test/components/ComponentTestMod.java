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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.components.v1.api.ComponentEvents;
import net.fabricmc.fabric.api.components.v1.api.ComponentType;

public class ComponentTestMod implements ModInitializer {
	public static final String MOD_ID = "fabric-components-api-v1-testmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static ComponentType<CreeperEntity, CreeperColor> CREEPER_COLOR = ComponentType.create(Identifier.of(ComponentTestMod.MOD_ID, "creeper_color"), builder -> {
		// Using the context object
		builder.listen(ComponentEvents.ENTITY_LOAD, CreeperEntity.class, CreeperColor::onLoad);
		// Not using the context object
		builder.listen(ComponentEvents.ENTITY_UNLOAD, CreeperEntity.class, CreeperColor::onUnload);
		builder.initializer(CreeperColor::new);
	});

	@Override
	public void onInitialize() {
		LOGGER.info("Started components API test mod");
	}
}
