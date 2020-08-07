package net.fabricmc.fabric.test.shield;

import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;

public class ShieldTestClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		FabricModelPredicateProviderRegistry.register(ShieldTest.SHIELD, new Identifier("blocking"),
				(stack, world, entity) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1 : 0);
	}
}
