package net.fabricmc.fabric.api.brewing;

import net.fabricmc.fabric.impl.brewing.PotionTypeRegistryImpl;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public interface PotionTypeRegistry {
	PotionTypeRegistry INSTANCE = new PotionTypeRegistryImpl();

	Item getItem(Identifier type);
	void registerPotionType(String id, Item item);
	void registerPotionType(Identifier id, Item item);
}
