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

package net.fabricmc.fabric.mixin.tag.extension;

import java.util.List;
import java.util.concurrent.Executor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.resource.ResourceManager;
import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.TagManagerLoader;

import net.fabricmc.fabric.impl.tag.extension.TagFactoryImpl;

@Mixin(TagManagerLoader.class)
public abstract class MixinTagManagerLoader {
	// RequiredTagListRegistry.forEach in reload.
	@SuppressWarnings("UnresolvedMixinReference")
	@Inject(method = "method_33179", at = @At("HEAD"), cancellable = true)
	private void method_33179(ResourceManager resourceManager, Executor executor, List<?> list, RequiredTagList<?> requiredTagList, CallbackInfo ci) {
		// Don't load dynamic registry tags now, we need to load them after the dynamic registry.
		if (TagFactoryImpl.isDynamic(requiredTagList)) {
			ci.cancel();
		}
	}
}
