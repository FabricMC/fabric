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

package net.fabricmc.fabric.test.mixin.resource.loader;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.packet.Packet;
import net.minecraft.registry.VersionedIdentifier;
import net.minecraft.server.network.SynchronizeRegistriesTask;

import net.fabricmc.fabric.test.resource.loader.BuiltinResourcePackTestMod;

@Mixin(SynchronizeRegistriesTask.class)
public class SynchronizeRegistriesTaskMixin {
	@Shadow
	@Final
	private List<VersionedIdentifier> knownPacks;

	@Inject(method = "syncRegistryAndTags", at = @At("HEAD"))
	public void syncRegistryAndTags(Consumer<Packet<?>> sender, Set<VersionedIdentifier> commonKnownPacks, CallbackInfo ci) {
		BuiltinResourcePackTestMod.LOGGER.info("Synchronizing registries with common known packs: {}", commonKnownPacks);

		if (!commonKnownPacks.containsAll(this.knownPacks)) {
			BuiltinResourcePackTestMod.LOGGER.error("(Ignore when not local client) Not all server mod data packs known to client. Missing: {}", this.knownPacks.stream().filter(knownPack -> !commonKnownPacks.contains(knownPack)).toList());
		}
	}
}
