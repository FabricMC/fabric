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

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import org.apache.commons.lang3.Validate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKey;

import net.fabricmc.fabric.impl.dimension.FailSoftMapCodec;

@Mixin(RegistryCodecs.class)
public class RegistryCodecsMixin {
	/**
	 * Fix the issue that cannot load world after uninstalling a dimension mod/datapack.
	 * After uninstalling a dimension mod/datapack, the dimension config in `level.dat` file cannot be deserialized.
	 * The solution is to make it fail-soft.
	 * Currently (1.19.3), `createKeyedRegistryCodec` is only used in dimension codec.
	 */
	@ModifyVariable(
			method = "createKeyedRegistryCodec",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lcom/mojang/serialization/Codec;unboundedMap(Lcom/mojang/serialization/Codec;Lcom/mojang/serialization/Codec;)Lcom/mojang/serialization/codecs/UnboundedMapCodec;",
					remap = false
			),
			ordinal = 1 // there are two local variables of `Codec` type. Modify the second.
	)
	private static <E> Codec<Map<RegistryKey<E>, E>> modifyCodecLocalVariable(
			Codec<Map<RegistryKey<E>, E>> originalVariable,
			RegistryKey<? extends Registry<E>> registryRef,
			Lifecycle lifecycle, Codec<E> elementCodec
	) {
		// make sure that it's not modifying the wrong variable
		Validate.isTrue(originalVariable instanceof UnboundedMapCodec<?, ?>);

		return new FailSoftMapCodec<>(RegistryKey.createCodec(registryRef), elementCodec);
	}
}
