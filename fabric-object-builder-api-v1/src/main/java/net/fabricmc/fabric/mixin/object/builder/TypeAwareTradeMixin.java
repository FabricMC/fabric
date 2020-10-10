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

package net.fabricmc.fabric.mixin.object.builder;

import java.util.Random;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.village.TradeOffer;

@Mixin(targets = "net/minecraft/village/TradeOffers$TypeAwareBuyForOneEmeraldFactory")
public abstract class TypeAwareTradeMixin {
	/**
	 * Vanilla will check the "VillagerType -> Item" map in the stream and throw an exception for villager types not specified in the map.
	 * This breaks any and all custom villager types.
	 * We want to prevent this default logic so modded villager types will work.
	 * So we return an empty stream so an exception is never thrown.
	 */
	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/DefaultedRegistry;stream()Ljava/util/stream/Stream;"))
	private <T> Stream<T> disableVanillaCheck(DefaultedRegistry<T> registry) {
		return Stream.empty();
	}

	/**
	 * To prevent "item" -> "air" trades, if the result of a type aware trade is air, make sure no offer is created.
	 */
	@Inject(method = "create(Lnet/minecraft/entity/Entity;Ljava/util/Random;)Lnet/minecraft/village/TradeOffer;", at = @At(value = "NEW", target = "net/minecraft/village/TradeOffer"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
	private void failOnNullItem(Entity entity, Random random, CallbackInfoReturnable<TradeOffer> cir, ItemStack buyingItem) {
		if (buyingItem.isEmpty()) { // Will return true for an "empty" item stack that had null passed in the ctor
			cir.setReturnValue(null); // Return null to prevent creation of empty trades
		}
	}
}
