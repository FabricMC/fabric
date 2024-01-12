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

package net.fabricmc.fabric.mixin.item;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.predicate.item.ItemPredicate;

import net.fabricmc.fabric.api.item.v1.CustomItemPredicate;
import net.fabricmc.fabric.api.item.v1.FabricItemPredicate;
import net.fabricmc.fabric.impl.item.ItemPredicateExtensions;

@Mixin(ItemPredicate.Builder.class)
abstract class ItemPredicateBuilderMixin implements FabricItemPredicate.FabricBuilder {
	@Unique
	private boolean hasCustom = false;

	@Unique
	private final ImmutableList.Builder<CustomItemPredicate> custom = new ImmutableList.Builder<>();

	@Override
	public ItemPredicate.Builder custom(CustomItemPredicate... predicate) {
		Preconditions.checkArgument(predicate.length > 0, "Must be 1 or more predicates");

		custom.add(predicate);
		hasCustom = true;
		return (ItemPredicate.Builder) (Object) this;
	}

	@ModifyReturnValue(method = "build", at = @At("RETURN"))
	private ItemPredicate addCustom(ItemPredicate original) {
		if (hasCustom) {
			((ItemPredicateExtensions) (Object) original).fabric_setCustom(custom.build());
		}

		return original;
	}
}
