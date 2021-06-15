package net.fabricmc.fabric.test.rendering.client;

import net.minecraft.client.render.SkyProperties;
import net.minecraft.world.dimension.DimensionType;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EnvironmentRenderers;
import net.fabricmc.fabric.api.client.rendering.v1.FabricSkyPropertyBuilder;

public class EnvironementalRenderingTest implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EnvironmentRenderers.registerSkyProperty(DimensionType.OVERWORLD_REGISTRY_KEY, FabricSkyPropertyBuilder.create().skyType(SkyProperties.SkyType.END).build());
	}
}
