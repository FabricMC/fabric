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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

@Mixin(RequiredTagListRegistry.class)
public class MixinRequiredTagListRegistry {
	@Unique
	private static final Map<RegistryKey<?>, RequiredTagList<?>> TAG_LISTS = new HashMap<>();

	@Inject(method = "register", at = @At("HEAD"), cancellable = true)
	private static void beforeRegister(RegistryKey<? extends Registry<?>> registryKey, String dataType, CallbackInfoReturnable<RequiredTagList<?>> cir) {
		if (TAG_LISTS.containsKey(registryKey)) {
			RequiredTagList<?> tagList = TAG_LISTS.get(registryKey);
			Preconditions.checkArgument(tagList.getDataType().equals(dataType), "Tag list for registry %s is already exist with data type %s", registryKey.getValue(), tagList.getDataType());
			cir.setReturnValue(tagList);
		}
	}

	@Inject(method = "register", at = @At("TAIL"))
	private static void afterRegister(RegistryKey<? extends Registry<?>> registryKey, String dataType, CallbackInfoReturnable<RequiredTagList<?>> cir) {
		TAG_LISTS.put(registryKey, cir.getReturnValue());
	}

	@Inject(method = "getBuiltinTags", at = @At("TAIL"), cancellable = true)
	private static void getBuiltinTags(CallbackInfoReturnable<Set<RequiredTagList<?>>> cir) {
		cir.setReturnValue(new HashSet<>(TAG_LISTS.values()));
	}
}
