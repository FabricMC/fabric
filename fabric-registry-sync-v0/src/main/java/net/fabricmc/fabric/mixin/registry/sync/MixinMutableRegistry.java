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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.impl.registry.sync.ModdableRegistry;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;

@Mixin(MutableRegistry.class)
public abstract class MixinMutableRegistry<T> extends Registry<T> implements ModdableRegistry {
	@Unique
	private boolean modded = false;

	@Unique
	private int preBootstrapHash = 0;

	@Inject(method = "add", at = @At("RETURN"))
	private <V extends T> void add(Identifier id, V entry, CallbackInfoReturnable<V> info) {
		onChange(id);
	}

	@Inject(method = "set", at = @At("RETURN"))
	private <V extends T> void set(int rawId, Identifier id, V entry, CallbackInfoReturnable<V> info) {
		onChange(id);
	}

	@Unique
	private void onChange(Identifier id) {
		if (RegistrySyncManager.postBootstrap) {
			markModded();
		} else if (!id.getNamespace().equals("minecraft")) {
			markModded();
		}
	}

	@Override
	public boolean isModded() {
		if (preBootstrapHash != 0) {
			if (getIds().hashCode() != preBootstrapHash) {
				markModded();
			}

			preBootstrapHash = 0;
		}

		return modded;
	}

	@Override
	public void markModded() {
		modded = true;
	}

	@Override
	public void storeIdHash(int hash) {
		preBootstrapHash = hash;
	}
}
