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

package net.fabricmc.fabric.mixin.registry.sync;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

@Mixin(RegistryKeys.class)
public class RegistryKeysMixin {
	@ModifyReturnValue(method = "getTagPath", at = @At("RETURN"))
	private static String prependDirectoryWithNamespace(String original, @Local(argsOnly = true) RegistryKey<? extends Registry<?>> registryRef) {
		Identifier id = registryRef.getValue();

		// Vanilla doesn't mark namespaces in the directories of tags at all,
		// so we prepend the directories with the namespace if it's a modded registry id.
		// No need to check DIRECTORIES, since this is only used by vanilla registries.
		if (!id.getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
			return "tags/" + id.getNamespace() + "/" + id.getPath();
		}

		return original;
	}
}
