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

import java.util.List;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.tag.TagManagerLoader;

import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;

/**
 * Capture deserialized tags, right at the end of the "apply" phase of the tag loader, for use by the xxx_tags_populated condition.
 * This gives access to these tags during the rest of the "apply" phase, when resource conditions are applied.
 */
@Mixin(TagManagerLoader.class)
public class TagManagerLoaderMixin {
	@Shadow
	private List<TagManagerLoader.RegistryTags<?>> registryTags;

	// lambda body inside thenAcceptAsync, in the reload method
	@Dynamic
	@Inject(
			method = "method_40098(Ljava/util/List;Ljava/lang/Void;)V",
			at = @At("RETURN")
	)
	private void hookApply(List<?> list, Void void_, CallbackInfo ci) {
		ResourceConditionsImpl.setTags(registryTags);
	}
}
