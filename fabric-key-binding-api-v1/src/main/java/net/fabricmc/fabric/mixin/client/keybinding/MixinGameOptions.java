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

package net.fabricmc.fabric.mixin.client.keybinding;

import java.util.Arrays;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;

import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;

@Mixin(GameOptions.class)
public abstract class MixinGameOptions implements GameOptionsAccessor {
	@Mutable
	@Final
	@Shadow
	public KeyBinding[] keysAll;

	private static KeyBinding[] registered;

	@Inject(at = @At("HEAD"), method = "load()V")
	public void loadHook(CallbackInfo info) {
		KeyBindingRegistryImpl.init(this, keysAll);
		this.keysAll = KeyBindingRegistryImpl.process();
	}

	@Inject(at = @At("HEAD"), method = {"write", "load"})
	public void writeUnregistered(final CallbackInfo info) {
		this.addUnregistered();
	}

	@Inject(at = @At("RETURN"), method = "write")
	public void cleanUpWrite(final CallbackInfo info) {
		this.keysAll = registered;
	}

	@Inject(at = @At("RETURN"), method = "load")
	public void cleanUpRead(final CallbackInfo info) {
		this.keysAll = registered;
	}

	private void addUnregistered() {
		int size = KeyBindingRegistryImpl.unregisteredKeyBindings.size();
		KeyBinding[] unregistered = KeyBindingRegistryImpl.unregisteredKeyBindings.elements();

		registered = this.keysAll;
		this.keysAll = Arrays.copyOf(registered, registered.length + size);
		System.arraycopy(unregistered, 0, this.keysAll, registered.length, size);
	}
}
