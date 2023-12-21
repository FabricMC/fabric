package net.fabricmc.fabric.test.item;

import net.minecraft.item.Item;
import net.minecraft.item.ShearsItem;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ShearsTest implements ModInitializer {
	public static final Item REAL_SHEARS = new ShearsItem(new FabricItemSettings().maxDamage(38)); // to show that ShearsItem will work
	public static final Item FAKE_SHEARS = new Item(new FabricItemSettings().maxDamage(38)); // to show that anything in fabric:shears will work

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, new Identifier("fabric-item-api-v1-testmod", "real_shears"), REAL_SHEARS);
		Registry.register(Registries.ITEM, new Identifier("fabric-item-api-v1-testmod", "fake_shears"), FAKE_SHEARS);
	}
}
