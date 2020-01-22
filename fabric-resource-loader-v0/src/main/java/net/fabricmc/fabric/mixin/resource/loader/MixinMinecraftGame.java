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

package net.fabricmc.fabric.mixin.resource.loader;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.resource.Format4ResourcePack;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;

import net.fabricmc.fabric.impl.resource.loader.ModResourcePackUtil;

@Mixin(MinecraftClient.class)
public class MixinMinecraftGame {
	@Shadow
	private ReloadableResourceManager resourceManager;

	private void fabric_modifyResourcePackList(List<ResourcePack> list) {
		List<ResourcePack> oldList = Lists.newArrayList(list);
		list.clear();

		boolean appended = false;

		for (int i = 0; i < oldList.size(); i++) {
			ResourcePack pack = oldList.get(i);
			list.add(pack);

			boolean isDefaultResources = pack instanceof DefaultResourcePack;

			if (!isDefaultResources && pack instanceof Format4ResourcePack) {
				MixinFormat4ResourcePack fixer = (MixinFormat4ResourcePack) pack;
				isDefaultResources = fixer.getParent() instanceof DefaultResourcePack;
			}

			if (isDefaultResources) {
				ModResourcePackUtil.appendModResourcePacks(list, ResourceType.CLIENT_RESOURCES);
				appended = true;
			}
		}

		if (!appended) {
			StringBuilder builder = new StringBuilder("Fabric could not find resource pack injection location!");

			for (ResourcePack rp : oldList) {
				builder.append("\n - ").append(rp.getName()).append(" (").append(rp.getClass().getName()).append(")");
			}

			throw new RuntimeException(builder.toString());
		}
	}

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;collect(Ljava/util/stream/Collector;)Ljava/lang/Object;"))
	public Object initResources(Stream<ResourcePack> stream, Collector collector) {
		List<ResourcePack> fabricResourcePacks = stream.collect(Collectors.toList());
		fabric_modifyResourcePackList(fabricResourcePacks);
		//noinspection unchecked
		return fabricResourcePacks.stream().collect(collector);
	}

	@Inject(method = "reloadResources", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ReloadableResourceManager;beginMonitoredReload(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Ljava/util/List;)Lnet/minecraft/resource/ResourceReloadMonitor;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	public void reloadResources(CallbackInfoReturnable<CompletableFuture> info, CompletableFuture<java.lang.Void> cf, List<ResourcePack> list) {
		fabric_modifyResourcePackList(list);
	}
}
