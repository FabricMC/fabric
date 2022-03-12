package net.fabricmc.fabric.impl.v1;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
//https://fabricmc.net/wiki/tutorial:tags#block_tags
public class TagRegistration<T> {
	public static final TagRegistration<Item> ITEM_TAG_REGISTRATION = new TagRegistration<>(Registry.ITEM_KEY);
	public static final TagRegistration<Block> BLOCK_TAG_REGISTRATION = new TagRegistration<>(Registry.BLOCK_KEY);
	public static final TagRegistration<Biome> BIOME_TAG_REGISTRATION = new TagRegistration<>(Registry.BIOME_KEY);
	public static final TagRegistration<Fluid> FLUID_TAG_REGISTRATION = new TagRegistration<>(Registry.FLUID_KEY);
	public static final TagRegistration<EntityType<?>> ENTITY_TYPE_TAG_REGISTRATION = new TagRegistration<>(Registry.ENTITY_TYPE_KEY);
	public static final TagRegistration<Enchantment> ENCHANTMENT_TAG_REGISTRATION = new TagRegistration<>(Registry.ENCHANTMENT_KEY);
	private final RegistryKey<Registry<T>> registryKey;

	private TagRegistration(RegistryKey<Registry<T>> registry) {
		registryKey = registry;
	}

	public TagKey<T> registerFabric(String tagId) {
		return TagKey.of(registryKey, new Identifier("fabric", tagId));
	}

	public TagKey<T> registerCommon(String tagId) {
		return TagKey.of(registryKey, new Identifier("c", tagId));
	}
}
