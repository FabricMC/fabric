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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;

@Mixin(BuiltinRegistries.class)
public class BuiltinRegistriesMixin {
	@Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/Registry;freeze()Lnet/minecraft/util/registry/Registry;"))
	private static Registry<?> unfreezeBultinRegistries(Registry<?> reg) {
		// Don't freeze
		return reg;
	}
}
