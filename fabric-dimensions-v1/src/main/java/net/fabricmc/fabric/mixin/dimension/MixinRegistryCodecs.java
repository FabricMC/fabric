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

package net.fabricmc.fabric.mixin.dimension;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import com.mojang.serialization.Codec;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GeneratorOptions;

import net.fabricmc.fabric.impl.dimension.FailSoftMapCodec;

@Mixin(RegistryCodecs.class)
public class MixinRegistryCodecs {
	/**
	 * @author FabricMC
	 * @reason make dimension deserialization fail soft
	 * In MC 1.18.2, this is only used in the codec of {@link GeneratorOptions}
	 */
	@Overwrite
	private static <T> Codec<Map<RegistryKey<T>, T>> registryMap(RegistryKey<? extends Registry<T>> registryRef, Codec<T> elementCodec) {
		return new FailSoftMapCodec<>(
				Identifier.CODEC.xmap(RegistryKey.createKeyFactory(registryRef), RegistryKey::getValue),
				elementCodec
		);
	}
}
