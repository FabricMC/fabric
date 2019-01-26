/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.mixin.loot;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.Gson;
import net.fabricmc.fabric.impl.loot.LootPoolAdder;
import net.fabricmc.fabric.impl.loot.LootSupplierHooks;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.LootManager;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.LootSupplier;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@Mixin(LootManager.class)
public class MixinLootManager {
	@Shadow @Final public static int jsonLength;
	@Shadow @Final private static Logger LOGGER;
	@Shadow @Final private static Gson gson;
	private static final String PATH = "loot_pools";
	private static final int LOOT_POOLS_LENGTH = PATH.length() + 1; // Includes the slash as well

	/**
	 * A map of loot pools adders with the keys being the targets.
	 */
	private final Multimap<Identifier, LootPoolAdder> lootPoolAdders = MultimapBuilder.hashKeys().hashSetValues().build();

	@Inject(method = "onResourceReload", at = @At("HEAD"))
	private void loadAdders(ResourceManager manager, CallbackInfo info) {
		manager.findResources(PATH, (name) -> name.endsWith(".json")).forEach(id2 -> {
			String originalPath = id2.getPath();
			Identifier id = new Identifier(id2.getNamespace(), originalPath.substring(LOOT_POOLS_LENGTH, originalPath.length() - jsonLength));
			try {
				try (Resource resource = manager.getResource(id2)) {
					LootPoolAdder adder = JsonHelper.deserialize(gson, IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8), LootPoolAdder.class);
					if (adder != null) {
						if (adder.targets.length == 0) {
							LOGGER.warn("[Fabric] Loot pool adder {} has no targets", id);
						}

						for (String target : adder.targets) {
							lootPoolAdders.put(new Identifier(target), adder);
						}
					}
				}
			} catch (Throwable t) {
				LOGGER.error("[Fabric] Couldn't read loot pool adder {} from {}", id, id2, t);
			}
		});
	}

	// TODO: Replace the redirect with this
    /*@Inject(method = "onResourceReload", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addLootPools(ResourceManager manager, CallbackInfo info, Iterator var2, Identifier identifier_1, String string_1, Identifier identifier_2, Resource resource_1, Object o, LootSupplier supplier) {
        addLootPools(supplier);
    }*/

	// TODO: Remove ugly redirect
	@Redirect(method = "onResourceReload", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/util/JsonHelper;deserialize(Lcom/google/gson/Gson;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;")))
	private Object put(Map map, Object key, Object value) {
		addLootPools((Identifier) key, (LootSupplier) value);
		return map.put(key, value);
	}

	private void addLootPools(Identifier id, LootSupplier supplier) {
		ArrayList<LootPool> pools = new ArrayList<>();

		for (LootPoolAdder adder : lootPoolAdders.get(id)) {
			pools.addAll(Arrays.asList(adder.pools));
		}

		((LootSupplierHooks) supplier).fabric_addPools(pools.toArray(new LootPool[0]));
	}
}
