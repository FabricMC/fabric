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

package net.fabricmc.fabric.test.dynamicregistry.tater;

import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.dynamic.RegistryElementCodec;

import net.fabricmc.fabric.test.dynamicregistry.DynamicRegistriesTestMod;

public class Tater {
	public static final Codec<Tater> CODEC = RecordCodecBuilder.create(instance -> {
		return instance
			.group(Codec.STRING.fieldOf("name").forGetter(Tater::getName))
			.apply(instance, Tater::new);
	});
	public static final Codec<Supplier<Tater>> REGISTRY_CODEC = RegistryElementCodec.of(DynamicRegistriesTestMod.TATER_KEY, CODEC);

	private final String name;

	public Tater(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return "Tater{name=" + this.name + "}";
	}
}
