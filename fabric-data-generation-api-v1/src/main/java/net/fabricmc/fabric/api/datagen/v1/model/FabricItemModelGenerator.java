package net.fabricmc.fabric.api.datagen.v1.model;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureMap;
import net.minecraft.item.Item;

public interface FabricItemModelGenerator {
	default void register(Item item, Model model, TextureMap textureMap) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	default void register(Item item, String suffix, Model model, TextureMap textureMap) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}
}
