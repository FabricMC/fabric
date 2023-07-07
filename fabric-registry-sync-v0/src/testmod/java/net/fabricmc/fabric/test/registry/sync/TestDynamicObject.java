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

package net.fabricmc.fabric.test.registry.sync;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record TestDynamicObject(String name, boolean usesNetworkCodec) {
	public static final Codec<TestDynamicObject> CODEC = codec(false);
	public static final Codec<TestDynamicObject> NETWORK_CODEC = codec(true);

	private static Codec<TestDynamicObject> codec(boolean networkCodec) {
		return RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("name").forGetter(TestDynamicObject::name)
		).apply(instance, name -> new TestDynamicObject(name, networkCodec)));
	}
}
