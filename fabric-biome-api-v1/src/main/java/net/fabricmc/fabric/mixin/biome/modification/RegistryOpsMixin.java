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

package net.fabricmc.fabric.mixin.biome.modification;

import com.mojang.serialization.DynamicOps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;

import net.fabricmc.fabric.impl.biome.modification.BiomeModificationImpl;

/**
 * This Mixin hooks int the creation of RegistryOps, which will currently load data pack contents into
 * the supplied dynamic registry manager, making it the point at which we should apply biome modifications.
 *
 * <p>There is generally the following order:
 * <ol>
 *     <li>{@link DynamicRegistryManager#create()} is used to create a dynamic registry manager with just
 *     entries from {@link net.minecraft.util.registry.BuiltinRegistries}</li>
 *     <li>Sometimes, Vanilla Minecraft will stop here, and use the {@link DynamicRegistryManager} as-is (examples: server.properties parsing, world creation screen).</li>
 *     <li>{@link RegistryOps#of(DynamicOps, ResourceManager, DynamicRegistryManager.Impl)} gets called with the manager, and a
 *     resource manager that contains the loaded data packs. This will pull in all worldgen objects from datapacks into the
 *     dynamic registry manager.</li>
 *     <li>After the worldgen objects are pulled in from the datapacks, this mixin will call the biome modification callback.</li>
 *     <li>In most cases, Vanilla will stop here and now use the dynamic registy manager to instantiate a server.</li>
 *     <li>Sometimes, i.e. when using the "re-create world feature", and a datapack throws an error, Vanilla will sometimes
 *     repeat the {@link RegistryOps#of(DynamicOps, ResourceManager, DynamicRegistryManager.Impl)} call on the same
 *     dynamic registry manager. We guard against this using {@link net.fabricmc.fabric.impl.biome.modification.BiomeModificationTracker}.</li>
 * </ol>
 */
@Mixin(RegistryOps.class)
public class RegistryOpsMixin {
	@Inject(method = "of", at = @At("RETURN"))
	private static <T> void afterCreation(DynamicOps<T> delegate, ResourceManager resourceManager, DynamicRegistryManager.Impl impl, CallbackInfoReturnable<RegistryOps<T>> ci) {
		BiomeModificationImpl.INSTANCE.modifyBiomes(impl);
	}
}
