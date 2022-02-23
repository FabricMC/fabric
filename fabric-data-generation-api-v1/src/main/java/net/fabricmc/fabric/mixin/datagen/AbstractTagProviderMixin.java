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

package net.fabricmc.fabric.mixin.datagen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.data.server.AbstractTagProvider;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

@Mixin(AbstractTagProvider.class)
public class AbstractTagProviderMixin {
	@ModifyArg(method = "getOrCreateTagBuilder", index = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/data/server/AbstractTagProvider$ObjectBuilder;<init>(Lnet/minecraft/tag/Tag$Builder;Lnet/minecraft/util/registry/Registry;Ljava/lang/String;)V"))
	private String injectModId(String str) {
		//noinspection ConstantConditions
		if ((Object) (this) instanceof FabricTagProvider fabricTagProvider) {
			return fabricTagProvider.getFabricDataGenerator().getModId();
		}

		return str;
	}
}
