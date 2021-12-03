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

package net.fabricmc.fabric.mixin.datagen;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.client.BlockStateDefinitionProvider;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.model.BlockStateModelGenerator;
import net.minecraft.data.client.model.BlockStateSupplier;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockStateDefinitionProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

@Mixin(BlockStateDefinitionProvider.class)
public class BlockStateDefinitionProviderMixin {
	@Shadow
	@Final
	private DataGenerator generator;

	@Unique
	private static ThreadLocal<DataGenerator> dataGeneratorThreadLocal = new ThreadLocal<>();

	@Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/data/client/model/BlockStateModelGenerator;register()V"))
	private void registerBlockStateModels(BlockStateModelGenerator instance) {
		if (((Object) this) instanceof FabricBlockStateDefinitionProvider fabricBlockStateDefinitionProvider) {
			fabricBlockStateDefinitionProvider.generateBlockStateModels(instance);
		} else {
			// Fallback to the vanilla registration when not a fabric provider
			instance.register();
		}
	}

	@Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/data/client/ItemModelGenerator;register()V"))
	private void registerItemModels(ItemModelGenerator instance) {
		if (((Object) this) instanceof FabricBlockStateDefinitionProvider fabricBlockStateDefinitionProvider) {
			fabricBlockStateDefinitionProvider.generateItemModels(instance);
		} else {
			// Fallback to the vanilla registration when not a fabric provider
			instance.register();
		}
	}

	@Inject(method = "run", at = @At("HEAD"))
	private void runHead(DataCache cache, CallbackInfo ci) {
		dataGeneratorThreadLocal.set(generator);
	}

	@Inject(method = "run", at = @At("TAIL"))
	private void runTail(DataCache cache, CallbackInfo ci) {
		dataGeneratorThreadLocal.remove();
	}

	@Inject(method = "method_25738", at = @At("HEAD"), cancellable = true)
	private static void filterBlocksForProcessingMod(Map<Block, BlockStateSupplier> map, Block block, CallbackInfoReturnable<Boolean> cir) {
		if (dataGeneratorThreadLocal.get() instanceof FabricDataGenerator dataGenerator) {
			if (!dataGenerator.isStrictValidationEnabled()) {
				cir.setReturnValue(false);
				return;
			}

			if (!Registry.BLOCK.getId(block).getNamespace().equals(dataGenerator.getModId())) {
				// Skip over blocks that are not from the mod we are processing.
				cir.setReturnValue(false);
			}
		}
	}

	@Inject(method = "method_25741", at = @At(value = "INVOKE", target = "Lnet/minecraft/data/client/model/ModelIds;getItemModelId(Lnet/minecraft/item/Item;)Lnet/minecraft/util/Identifier;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private static void filterItemsForProcessingMod(Set<Item> set, Map<Identifier, Supplier<JsonElement>> map, Block block, CallbackInfo ci, Item item) {
		if (dataGeneratorThreadLocal.get() instanceof FabricDataGenerator dataGenerator) {
			if (!dataGenerator.isStrictValidationEnabled()) {
				ci.cancel();
				return;
			}

			if (!Registry.ITEM.getId(item).getNamespace().equals(dataGenerator.getModId())) {
				// Skip over any items from other mods.
				ci.cancel();
			}
		}
	}
}
