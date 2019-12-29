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
import net.minecraft.world.level.LevelGeneratorType;

import net.fabricmc.fabric.impl.levelgenerator.IFabricLevelType;

@Mixin(MinecraftDedicatedServer.class)
public class MixinMinecraftDedicatedServer {
	@Shadow
	@Final
	private static Logger LOGGER;

	@Shadow
	@Final
	private ServerPropertiesLoader propertiesLoader;

	@ModifyVariable(method = "setupServer", at = @At("STORE"))
	LevelGeneratorType replaceLevelGeneratorType(LevelGeneratorType unused) {
		String fabriclevelType = ((IFabricLevelType) propertiesLoader.getPropertiesHandler()).getFabriclevelType();
		LevelGeneratorType levelGeneratorType = LevelGeneratorType.getTypeFromName(fabriclevelType);

		if (levelGeneratorType == null) {
			LOGGER.error("Incorrect level-type \"" + fabriclevelType + "\", falling back to default");
			return LevelGeneratorType.DEFAULT;
		}

		return levelGeneratorType;
	}
}
