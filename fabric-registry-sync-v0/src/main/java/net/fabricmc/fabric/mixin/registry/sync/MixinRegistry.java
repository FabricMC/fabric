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

import java.util.EnumSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeHolder;
import net.fabricmc.fabric.impl.registry.sync.FabricRegistry;
import net.fabricmc.fabric.impl.registry.sync.HashedRegistry;

@Mixin(Registry.class)
public abstract class MixinRegistry<T> implements RegistryAttributeHolder, FabricRegistry, HashedRegistry {
	@Unique
	private final EnumSet<RegistryAttribute> attributes = EnumSet.noneOf(RegistryAttribute.class);

	@Unique
	private int previousHash = -1;

	@Shadow
	public abstract Set<Identifier> getIds();

	/**
	 * This is used to denote backwards compatibility, when false default attributes will be applied.
	 * This is set to true when using FabricRegistryBuilder
	 */
	@Unique
	private boolean builtWithBuilder = false;

	@Override
	public RegistryAttributeHolder addAttribute(RegistryAttribute attribute) {
		attributes.add(attribute);
		return this;
	}

	@Override
	public boolean hasAttribute(RegistryAttribute attribute) {
		return attributes.contains(attribute);
	}

	@Override
	public void build(Set<RegistryAttribute> attributes) {
		builtWithBuilder = true;
		this.attributes.addAll(attributes);
	}

	@Override
	public boolean builtByBuilder() {
		return builtWithBuilder;
	}

	@Override
	public int getStoredHash() {
		return previousHash;
	}

	@Override
	public int storeHash() {
		return previousHash = getIds().hashCode();
	}
}
