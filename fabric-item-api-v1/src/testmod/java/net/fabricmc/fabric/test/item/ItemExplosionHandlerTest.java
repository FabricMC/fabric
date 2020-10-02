package net.fabricmc.fabric.test.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

public final class ItemExplosionHandlerTest implements ModInitializer {
	private static final Item TEST = Registry.register(Registry.ITEM, new Identifier("fabric-item-api-v1-testmod"), new TestItem());

	@Override
	public void onInitialize() {
	}

	private static final class TestItem extends Item {
		TestItem() {
			super(new FabricItemSettings().explosionHandler((entity, source, amount) -> true).group(ItemGroup.MISC));
		}
	}
}
