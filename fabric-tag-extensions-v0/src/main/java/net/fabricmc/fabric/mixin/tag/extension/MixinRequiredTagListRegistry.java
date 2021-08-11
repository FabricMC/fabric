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

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.RequiredTagListRegistry;

import net.fabricmc.fabric.impl.tag.extension.TagFactoryImpl;

@Mixin(RequiredTagListRegistry.class)
public class MixinRequiredTagListRegistry {
	@Inject(method = "getBuiltinTags", at = @At("TAIL"), cancellable = true)
	private static void getBuiltinTags(CallbackInfoReturnable<Set<RequiredTagList<?>>> cir) {
		// Add tag lists registered on fabric to the map.
		Set<RequiredTagList<?>> set = new HashSet<>();
		set.addAll(cir.getReturnValue());
		set.addAll(TagFactoryImpl.TAG_LISTS.values());
		cir.setReturnValue(set);
	}
}
