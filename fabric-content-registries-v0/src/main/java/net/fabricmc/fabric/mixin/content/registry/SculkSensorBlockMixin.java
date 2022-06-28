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

package net.fabricmc.fabric.mixin.content.registry;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.SculkSensorBlock;

@Mixin(SculkSensorBlock.class)
public class SculkSensorBlockMixin {
	/**
	 * Redirects the call to {@linkplain Object2IntMaps#unmodifiable(Object2IntMap)} in initialization of {@linkplain SculkSensorBlock#FREQUENCIES}.
	 */
	@Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2IntMaps;unmodifiable(Lit/unimi/dsi/fastutil/objects/Object2IntMap;)Lit/unimi/dsi/fastutil/objects/Object2IntMap;"))
	private static <K> Object2IntMap<K> makeFrequenciesMapModifiable(Object2IntMap<? extends K> m) {
		return (Object2IntMap<K>) m;
	}
}
