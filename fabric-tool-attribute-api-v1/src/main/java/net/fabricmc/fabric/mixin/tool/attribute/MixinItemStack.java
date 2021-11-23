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

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.fabricmc.fabric.api.tool.attribute.v1.ToolManager;
import net.fabricmc.fabric.impl.tool.attribute.DynamicToolContext;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
	@Inject(at = @At("RETURN"), method = "isSuitableFor", cancellable = true)
	public void isEffectiveOn(BlockState state, CallbackInfoReturnable<Boolean> info) {
		info.setReturnValue(ToolManager.handleIsEffectiveOnIgnoresVanilla(state, (ItemStack) (Object) this, DynamicToolContext.get(), info.getReturnValueZ()));
	}

	@Inject(at = @At("RETURN"), method = "getMiningSpeedMultiplier", cancellable = true)
	public void getMiningSpeedMultiplier(BlockState state, CallbackInfoReturnable<Float> info) {
		float customSpeed = ToolManager.handleBreakingSpeedIgnoresVanilla(state, (ItemStack) (Object) this, DynamicToolContext.get());

		if (info.getReturnValueF() < customSpeed) {
			info.setReturnValue(customSpeed);
		}
	}

	@ModifyVariable(method = "getAttributeModifiers", at = @At(value = "RETURN", shift = At.Shift.BEFORE))
	public Multimap<EntityAttribute, EntityAttributeModifier> modifyAttributeModifiersMap(Multimap<EntityAttribute, EntityAttributeModifier> multimap, EquipmentSlot slot) {
		ItemStack stack = (ItemStack) (Object) this;

		// Only perform our custom operations if the tool being operated on is dynamic.
		if (stack.getItem() instanceof DynamicAttributeTool holder) {
			// The Multimap passed in is not ordered, so we need to re-assemble the vanilla and modded attributes
			// into a custom, ordered Multimap. If this step is not done, and both vanilla + modded attributes
			// exist at once, the item tooltip attribute lines will randomly switch positions.
			LinkedListMultimap<EntityAttribute, EntityAttributeModifier> orderedAttributes = LinkedListMultimap.create();
			// First, add all vanilla attributes to our ordered Multimap.
			orderedAttributes.putAll(multimap);
			// Second, calculate the dynamic attributes, and add them at the end of our Multimap.
			orderedAttributes.putAll(holder.getDynamicModifiers(slot, stack, DynamicToolContext.get()));
			return orderedAttributes;
		}

		return multimap;
	}
}
