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

import java.util.List;
import java.util.Optional;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.dynamic.Codecs;

import net.fabricmc.fabric.api.item.v1.CustomItemPredicate;
import net.fabricmc.fabric.api.item.v1.FabricItemPredicate;
import net.fabricmc.fabric.impl.item.ItemPredicateExtensions;
import net.fabricmc.fabric.impl.item.VanillaItemPredicate;

@Mixin(ItemPredicate.class)
abstract class ItemPredicateMixin implements ItemPredicateExtensions, FabricItemPredicate {
	@Shadow
	@Final
	@Mutable
	public static Codec<ItemPredicate> CODEC;

	@Unique
	private List<CustomItemPredicate> custom = List.of();

	@Override
	public List<CustomItemPredicate> custom() {
		return custom;
	}

	@Override
	public void fabric_setCustom(List<CustomItemPredicate> custom) {
		this.custom = custom;
	}

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void modifyCodec(CallbackInfo ci) {
		Codec<ItemPredicate> vanillaCodec = CODEC;
		VanillaItemPredicate.register(vanillaCodec);

		Codec<CustomItemPredicate> customCodec = CustomItemPredicate.REGISTRY.getCodec().dispatch("fabric:type", CustomItemPredicate::getCodec, c -> c);

		CODEC = Codecs.either(Codecs.nonEmptyList(customCodec.listOf()), vanillaCodec).xmap(e -> e.map(l -> {
			ItemPredicate predicate = new ItemPredicate(Optional.empty(), Optional.empty(), NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, List.of(), List.of(), Optional.empty(), Optional.empty());
			((ItemPredicateMixin) (Object) predicate).fabric_setCustom(l);
			return predicate;
		}, r -> r), predicate -> {
			// Encode using the replacement codec if custom exists, otherwise use vanilla codec.
			if (!predicate.custom().isEmpty()) {
				return Either.left(predicate.custom());
			} else {
				return Either.right(predicate);
			}
		});
	}

	@Inject(method = "test", at = @At("HEAD"), cancellable = true)
	private void customTest(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (custom.isEmpty()) return;

		for (CustomItemPredicate predicate : custom) {
			if (!predicate.test(stack)) {
				cir.setReturnValue(false);
				return;
			}
		}

		// The vanilla instance will get wrapped as VanillaItemPredicate and added to the custom list,
		// therefore the test should already be called in the loop above.
		cir.setReturnValue(true);
	}
}
