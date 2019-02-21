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
import net.fabricmc.fabric.impl.resources.ResourceManagerHelperImpl;
import net.minecraft.resource.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableResourceManagerImpl.class)
public class MixinReloadableResourceManagerImpl {
	@Shadow
	private List<ResourceReloadListener> field_17935;
	@Shadow
	private List<ResourceReloadListener> field_17936;
	@Shadow
	private ResourceType type;

	@Inject(at = @At("HEAD"), method = "reload")
	public void reload(Executor var1, Executor var2, List<ResourcePack> packs, CompletableFuture future, CallbackInfoReturnable<CompletableFuture> info) {
		ResourceManagerHelperImpl.sort(type, field_17935);
		ResourceManagerHelperImpl.sort(type, field_17936);
	}
}
