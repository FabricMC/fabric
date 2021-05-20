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

package net.fabricmc.fabric.mixin.tool.attribute;

import java.util.List;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.fabricmc.fabric.api.tool.attribute.v1.ToolManager;
import net.fabricmc.fabric.impl.tool.attribute.ItemStackContext;

@Mixin(ItemStack.class)
public abstract class MixinItemStack implements ItemStackContext {
	@Unique
	@Nullable
	private LivingEntity contextEntity = null;

	@Inject(at = @At("RETURN"), method = "isEffectiveOn", cancellable = true)
	public void isEffectiveOn(BlockState state, CallbackInfoReturnable<Boolean> info) {
		info.setReturnValue(ToolManager.handleIsEffectiveOnIgnoresVanilla(state, (ItemStack) (Object) this, null, info.getReturnValueZ()));
	}

	@Inject(at = @At("RETURN"), method = "getMiningSpeedMultiplier", cancellable = true)
	public void getMiningSpeedMultiplier(BlockState state, CallbackInfoReturnable<Float> info) {
		float customSpeed = ToolManager.handleBreakingSpeedIgnoresVanilla(state, (ItemStack) (Object) this, null);

		if (info.getReturnValueF() < customSpeed) {
			info.setReturnValue(customSpeed);
		}
	}

	// This inject stores context about the player viewing an ItemStack's tooltip before attributes are calculated.
	@Environment(EnvType.CLIENT)
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getAttributeModifiers(Lnet/minecraft/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"), method = "getTooltip")
	private void storeTooltipAttributeEntityContext(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
		contextEntity = player;
	}

	// This inject removes context specified in the previous inject.
	// This is done to prevent issues with other mods calling getAttributeModifiers.
	@Environment(EnvType.CLIENT)
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getAttributeModifiers(Lnet/minecraft/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;", shift = At.Shift.AFTER), method = "getTooltip")
	private void revokeTooltipAttributeEntityContext(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
		contextEntity = null;
	}

	@Inject(at = @At("RETURN"), method = "getAttributeModifiers", cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	public void getAttributeModifiers(EquipmentSlot slot, CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> info, Multimap<EntityAttribute, EntityAttributeModifier> multimap) {
		ItemStack stack = (ItemStack) (Object) this;

		// Only perform our custom operations if the tool being operated on is dynamic.
		if (stack.getItem() instanceof DynamicAttributeTool) {
			// The Multimap passed in is not ordered, so we need to re-assemble the vanilla and modded attributes
			// into a custom, ordered Multimap. If this step is not done, and both vanilla + modded attributes
			// exist at once, the item tooltip attribute lines will randomly switch positions.
			LinkedListMultimap<EntityAttribute, EntityAttributeModifier> orderedAttributes = LinkedListMultimap.create();

			// First, add all vanilla attributes to our ordered Multimap.
			orderedAttributes.putAll(multimap);

			// Second, calculate the dynamic attributes, and add them at the end of our Multimap.
			DynamicAttributeTool holder = (DynamicAttributeTool) stack.getItem();
			orderedAttributes.putAll(holder.getDynamicModifiers(slot, stack, contextEntity));
			info.setReturnValue(orderedAttributes);
		}
	}

	@Override
	public void fabricToolAttributes_setContext(@Nullable LivingEntity contextEntity) {
		this.contextEntity = contextEntity;
	}
}
