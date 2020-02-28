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

package net.fabricmc.fabric.mixin.level.generator;

import java.util.Properties;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.dedicated.AbstractPropertiesHandler;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.util.Identifier;
import net.minecraft.world.level.LevelGeneratorType;

import net.fabricmc.fabric.impl.level.generator.FabricLevelTypeProvider;

@Mixin(ServerPropertiesHandler.class)
public abstract class MixinServerPropertiesHandler extends AbstractPropertiesHandler<ServerPropertiesHandler> implements FabricLevelTypeProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private Identifier fabricLevelType;

	public MixinServerPropertiesHandler(Properties properties) {
		super(properties);
	}

	@SuppressWarnings("unchecked")
	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/ServerPropertiesHandler;get(Ljava/lang/String;Ljava/util/function/Function;Ljava/util/function/Function;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 2))
	private <V> V replaceLevelType(ServerPropertiesHandler serverPropertiesHandler, String prop, Function<String, V> function, Function<V, String> function2, V defaultObject) {
		if (!prop.equals("level-type")) throw new RuntimeException("This mixin should only be applied to level-type");

		// If level-type doesn't exist, fallback to default behavior
		if (getProperties().get(prop) == null) {
			get("level-type", LevelGeneratorType::getTypeFromName, LevelGeneratorType::getName, LevelGeneratorType.DEFAULT);
		}

		// Parse String as segments
		String value = getProperties().get(prop).toString();
		String[] levelType = value.split(":");

		// Check for vanilla levelGenerators
		LevelGeneratorType levelGeneratorType = LevelGeneratorType.getTypeFromName(value);
		if (levelGeneratorType != null) return (V) levelGeneratorType;

		// Skip namespace if not supplied
		if (levelType.length == 1) {
			fabricLevelType = new Identifier("fabric_omitted_namespace", levelType[0]);
			return null;
		}

		// level-type should only have 2 elements separated by ":"
		if (levelType.length != 2) {
			LOGGER.error("Level-type identifier \"" + value + "\" has more than one ':' character");
			return null;
		}

		// Parse string to identifier
		fabricLevelType = new Identifier(levelType[0], levelType[1]);
		return null;
	}

	@Override
	public Identifier getFabricLevelType() {
		return fabricLevelType;
	}
}
