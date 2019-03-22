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
