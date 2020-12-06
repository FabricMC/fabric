/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.client.rendering.v1;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.rendering.ArmorRenderingRegistryImpl;

/**
 * A class for registering custom armor models and textures for {@link Item}, to be provided by a {@link ModelProvider} or {@link TextureProvider}.
 *
 * <p>This can be used to replace existing vanilla armor models and textures conditionally, however each {@link Item}
 * instance can only allow one {@link ModelProvider} or {@link TextureProvider} respectively, causing potential conflicts
 * with other mods if you replace the model or texture for vanilla items. Consider using a separate item instead.</p>
 *
 * <p>A custom model would probably also require a custom texture to go along it, the model will use the vanilla texture if it is undefined.</p>
 *
 * <p>Since armor textures identifier in vanilla is hardcoded to be in the {@code minecraft} namespace, this registry can also be
 * used to use a custom namespace if desired.</p>
 */
@Environment(EnvType.CLIENT)
public final class ArmorRenderingRegistry {
	private ArmorRenderingRegistry() {
	}

	/**
	 * Registers a provider for custom armor models for an item.
	 *
	 * @param provider the provider for the model
	 * @param items    the items to be registered for
	 */
	public static void registerModel(@Nullable ModelProvider provider, Item... items) {
		registerModel(provider, Arrays.asList(items));
	}

	/**
	 * Registers a provider for custom armor models for an item.
	 *
	 * @param provider the provider for the model
	 * @param items    the items to be registered for
	 */
	public static void registerModel(@Nullable ModelProvider provider, Iterable<Item> items) {
		ArmorRenderingRegistryImpl.registerModel(provider, items);
	}

	/**
	 * Registers a provider for custom texture models for an item.
	 *
	 * @param provider the provider for the texture
	 * @param items    the items to be registered for
	 */
	public static void registerTexture(@Nullable TextureProvider provider, Item... items) {
		registerTexture(provider, Arrays.asList(items));
	}

	/**
	 * Registers a provider for custom texture models for an item.
	 *
	 * @param provider the provider for the texture
	 * @param items    the items to be registered for
	 */
	public static void registerTexture(@Nullable TextureProvider provider, Iterable<Item> items) {
		ArmorRenderingRegistryImpl.registerTexture(provider, items);
	}

	/**
	 * Register simple armor items to use the vanilla armor file name under the mods namespace.
	 *
	 * @param identifier The namespace + path to use for the armor texture location.
	 * @param items the items to be registered
	 */
	public static void registerSimpleTexture(Identifier identifier, Item... items) {
		registerTexture((entity, stack, slot, secondLayer, suffix, defaultTexture) -> {
			return new Identifier(identifier.getNamespace(), "textures/models/armor/" + identifier.getPath() + "_layer_" + (secondLayer ? 2 : 1) + (suffix == null ? "" : "_" + suffix) + ".png");
		}, items);
	}

	/**
	 * Gets the model of the armor piece.
	 *
	 * @param entity       The entity equipping the armor
	 * @param stack        The item stack of the armor
	 * @param slot         The slot which the armor is in
	 * @param defaultModel The default model that vanilla provides
	 * @return The model of the armor piece.
	 */
	@NotNull
	public static BipedEntityModel<LivingEntity> getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, BipedEntityModel<LivingEntity> defaultModel) {
		return ArmorRenderingRegistryImpl.getArmorModel(entity, stack, slot, defaultModel);
	}

	/**
	 * Gets the armor texture {@link net.minecraft.util.Identifier}.
	 *
	 * @param entity         The entity equipping the armor
	 * @param stack          The item stack of the armor
	 * @param slot           The slot which the armor is in
	 * @param secondLayer	 True if using the second texture layer
	 * @param suffix         The texture suffix, used in vanilla by {@link net.minecraft.item.DyeableArmorItem}
	 * @param defaultTexture The default vanilla texture identifier
	 * @return the custom armor texture identifier, return null to use the vanilla ones. Defaulted to the item's registry id.
	 */
	@NotNull
	public static Identifier getArmorTexture(LivingEntity entity, ItemStack stack, EquipmentSlot slot, boolean secondLayer, @Nullable String suffix, Identifier defaultTexture) {
		return ArmorRenderingRegistryImpl.getArmorTexture(entity, stack, slot, secondLayer, suffix, defaultTexture);
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface ModelProvider {
		/**
		 * Gets the model of the armor piece.
		 *
		 * @param entity       The entity equipping the armor
		 * @param stack        The item stack of the armor
		 * @param slot         The slot which the armor is in
		 * @param defaultModel The default vanilla armor model
		 * @return The model of the armor piece. Should never be null.
		 */
		@NotNull
		BipedEntityModel<LivingEntity> getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, BipedEntityModel<LivingEntity> defaultModel);
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface TextureProvider {
		/**
		 * Gets the armor texture {@link net.minecraft.util.Identifier}.
		 *
		 * @param entity         The entity equipping the armor
		 * @param stack          The item stack of the armor
		 * @param slot           The slot which the armor is in
		 * @param defaultTexture The default vanilla texture identifier
		 * @return the custom armor texture identifier. Should never be null.
		 */
		@NotNull
		Identifier getArmorTexture(LivingEntity entity, ItemStack stack, EquipmentSlot slot, boolean secondLayer, @Nullable String suffix, Identifier defaultTexture);
	}
}
