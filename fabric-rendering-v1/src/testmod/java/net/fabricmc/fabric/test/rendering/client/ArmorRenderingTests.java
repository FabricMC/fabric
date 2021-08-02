package net.fabricmc.fabric.test.rendering.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderingRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class ArmorRenderingTests implements ClientModInitializer {
	private BipedEntityModel<LivingEntity> armorModel;
	private Identifier texture = new Identifier("textures/block/dirt.png");
	@Override
	public void onInitializeClient() {
		ArmorRenderingRegistry.INSTANCE.register((matrices, vertexConsumers, stack, entity, slot, light, model) -> {
			if (armorModel == null) {
				armorModel = new BipedEntityModel<>(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EntityModelLayers.PLAYER_OUTER_ARMOR));
			}
			model.setAttributes(armorModel);
			armorModel.setVisible(false);
			armorModel.body.visible = slot == EquipmentSlot.CHEST;
			armorModel.leftArm.visible = slot == EquipmentSlot.CHEST;
			armorModel.rightArm.visible = slot == EquipmentSlot.CHEST;
			armorModel.head.visible = slot == EquipmentSlot.HEAD;
			ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, armorModel, texture);
			}, Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE);
	}
}
