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

package net.fabricmc.fabric.mixin.levelgenerator;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import net.minecraft.util.Identifier;
import net.minecraft.world.level.LevelGeneratorType;

import net.fabricmc.fabric.impl.levelgenerator.FabricLevelGeneratorType;
import net.fabricmc.fabric.impl.levelgenerator.FabricLevelTypeProvider;

@Mixin(MinecraftDedicatedServer.class)
public final class MixinMinecraftDedicatedServer {
	@Shadow
	@Final
	private static Logger LOGGER;

	@Shadow
	@Final
	private ServerPropertiesLoader propertiesLoader;

	@ModifyVariable(method = "setupServer", at = @At("STORE"))
	private LevelGeneratorType replaceLevelGeneratorType(LevelGeneratorType levelGeneratorType) {
		// Check if we are using vanilla LevelGeneratorType
		if (levelGeneratorType != null) return levelGeneratorType;

		// Get fabricLevelType identifier added with MixinServerPropertiesHandler
		Identifier fabricLevelType = ((FabricLevelTypeProvider) propertiesLoader.getPropertiesHandler()).getFabricLevelType();

		if (fabricLevelType != null) {
			// Give ability to skip namespace if mods levelGenerators don't have same name
			// If they do, first one will be used
			if (fabricLevelType.getNamespace().equals("fabric_omitted_namespace")) {
				return FabricLevelGeneratorType.getTypeFromPath(fabricLevelType.getPath());
			} else {
				return LevelGeneratorType.getTypeFromName(fabricLevelType.toString().replaceAll(":", "."));
			}
		}

		// Fallback to LevelGeneratorType.DEFAULT
		LOGGER.error("Incorrect level-type, falling back to default");
		return LevelGeneratorType.DEFAULT;
	}
}
