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

package net.fabricmc.fabric.mixin.object.builder;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// Suppress warnings when a {@link} reference a private method since it's invoked by the mixin
@SuppressWarnings("JavadocReference")
@Mixin(TrunkPlacerType.class)
public interface TrunkPlacerTypeInvoker {
	/**
	 * Invokes the {@link TrunkPlacerType#register} method that creates a new {@link TrunkPlacerType}, registers it and returns it
	 */
	@Invoker
	static <T extends TrunkPlacer> TrunkPlacerType<T> invokeRegister(String id, Codec<T> codec) {
		throw new AssertionError();
	}
}
