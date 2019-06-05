package net.fabricmc.fabric.impl.brewing;

import net.fabricmc.fabric.api.brewing.PotionTypeRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class PotionTypeRegistryImpl implements PotionTypeRegistry {
	private final Map<Identifier, Item> typeItems = new HashMap<>();

	public PotionTypeRegistryImpl() {
		registerPotionType("normal", Items.POTION);
		registerPotionType("splash", Items.SPLASH_POTION);
		registerPotionType("lingering", Items.LINGERING_POTION);
	}

	public Item getItem(Identifier id) { return typeItems.getOrDefault(id, Items.POTION); }
	public void registerPotionType(String id, Item item) { registerPotionType(new Identifier(id.toLowerCase()), item);}
	public void registerPotionType(Identifier id, Item item) { typeItems.put(id, item); }
}
