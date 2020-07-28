package net.fabricmc.fabric.test.rendering.client;

import java.util.Collections;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderingRegistry;

@Environment(EnvType.CLIENT)
public class CustomArmorTests implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Item customModeledArmor, customTexturedArmor;
		Registry.register(Registry.ITEM, new Identifier("fabric-rendering-v1-testmod:custom_modeled_armor"),
				customModeledArmor = new ArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.COMBAT)));

		CustomArmorModel model = new CustomArmorModel(1.0F);
		ArmorRenderingRegistry.registerModel((entity, stack, slot, defaultModel) -> model, customModeledArmor);
		ArmorRenderingRegistry.registerTexture((entity, stack, slot, defaultTexture) ->
				"fabric-rendering-v1-testmod:thing/i_have_a_cube.png", customModeledArmor);

		Registry.register(Registry.ITEM, new Identifier("fabric-rendering-v1-testmod:custom_textured_armor"),
				customTexturedArmor = new ArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.COMBAT)));

		ArmorRenderingRegistry.registerTexture((entity, stack, slot, defaultTexture) ->
				"fabric-rendering-v1-testmod:thing/amazing.png", customTexturedArmor);
	}

	private static class CustomArmorModel extends BipedEntityModel<LivingEntity> {
		private final ModelPart part;

		CustomArmorModel(float scale) {
			super(scale, 0, 1, 1);
			part = new ModelPart(this, 0, 0);
			part.addCuboid(-5F, 0F, 2F, 10, 10, 10);
			part.setPivot(0F, 0F, 0F);
			part.mirror = true;
		}

		@Override
		protected Iterable<ModelPart> getBodyParts() {
			return Collections.singleton(part);
		}

		@Override
		protected Iterable<ModelPart> getHeadParts() {
			return Collections::emptyIterator;
		}
	}
}
