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

package net.fabricmc.fabric.mixin.resources;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.impl.resources.ModResourcePackUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.DefaultClientResourcePack;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(MinecraftClient.class)
public class MixinMinecraftGame {
    @Shadow
    private ReloadableResourceManager resourceManager;

    @Inject(method = "reloadResources()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ReloadableResourceManager;reload(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/List;Ljava/util/concurrent/CompletableFuture;)Ljava/util/concurrent/CompletableFuture;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    public void reloadResources(CallbackInfo info, List<ResourcePack> list) {
    	List<ResourcePack> oldList = Lists.newArrayList(list);
    	list.clear();
    	for (int i = 0; i < oldList.size(); i++) {
    		ResourcePack pack = oldList.get(i);
    		list.add(pack);

    		if (pack instanceof DefaultClientResourcePack) {
			    ModResourcePackUtil.appendModResourcePacks(list, ResourceType.ASSETS);
		    }
	    }
    }
}
