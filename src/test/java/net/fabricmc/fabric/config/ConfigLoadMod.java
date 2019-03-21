package net.fabricmc.fabric.config;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.config.ConfigFile;
import net.fabricmc.fabric.api.config.FabricConfigManager;

import java.util.ArrayList;
import java.util.List;

public class ConfigLoadMod implements ModInitializer {
	@Override
	public void onInitialize() {
		FabricConfigManager.loadConfig(ModConfig.class);
	}

	@ConfigFile("ModConfig")
	public class ModConfig {

		@Comment("A test int.")
		public int value = 5;

		@Comment("A test list.")
		public List<String> allowedNames = new ArrayList<>();

		@Comment("A test string.")
		public String name = "Fabric!";
	}
}
