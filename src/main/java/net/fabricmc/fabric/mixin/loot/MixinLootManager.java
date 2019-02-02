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

import net.fabricmc.fabric.api.events.LootTableLoadingCallback;
import net.fabricmc.fabric.api.loot.FabricLootSupplier;
import net.fabricmc.fabric.util.HandlerArray;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.LootManager;
import net.minecraft.world.loot.LootSupplier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LootManager.class)
public class MixinLootManager {
	@Shadow @Final private Map<Identifier, LootSupplier> suppliers;

	@Inject(method = "onResourceReload", at = @At("RETURN"))
	private void onResourceReload(ResourceManager manager, CallbackInfo info) {
		suppliers.forEach((id, supplier) -> {
			LootTableLoadingCallback[] handlers = ((HandlerArray<LootTableLoadingCallback>) LootTableLoadingCallback.REGISTRY)
					.getBackingArray();

			for (LootTableLoadingCallback handler : handlers) {
				handler.accept(id, (FabricLootSupplier) supplier);
			}
		});
	}
}
