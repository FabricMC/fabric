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

package net.fabricmc.fabric.config;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.config.ConfigBuilder;

public class ConfigLoadMod implements ModInitializer {
	@Override
	public void onInitialize() {
		//Super basic config
		{
			// ./config/modid.json
			ModConfig simpleConfig = ConfigBuilder.builder("modid").getConfig(ModConfig.class);
		}

		//A mod with more than one config, in sub dirs
		{
			ConfigBuilder configBuilder = ConfigBuilder.builder("modid2");
			// ./config/modid2/client.json
			ModConfig clientConfig = configBuilder.getConfig(ModConfig.class, "client");
			// ./config/modid2/common.json
			ModConfig commonConfig = configBuilder.getConfig(ModConfig.class, "common");
		}
	}

}
