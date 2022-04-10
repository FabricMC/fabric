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

package net.fabricmc.fabric.mixin.resource.conditions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.DataPackContents;
import net.minecraft.util.registry.DynamicRegistryManager;

import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;

/**
 * Clear the tags captured by {@link DataPackContentsMixin}.
 * This must happen after the resource reload is complete, to ensure that the tags remain available throughout the entire "apply" phase.
 */
@Mixin(DataPackContents.class)
public class DataPackContentsMixin {
	@Inject(
			method = "refresh",
			at = @At("HEAD")
	)
	public void hookRefresh(DynamicRegistryManager dynamicRegistryManager, CallbackInfo ci) {
		ResourceConditionsImpl.clearTags();
	}
}
