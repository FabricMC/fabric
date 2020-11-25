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

package net.fabricmc.fabric.mixin.structure;

import java.util.Collections;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Identifier;

/**
 * This fixes a rare CME in the StructureManager when using Java 11 or newer.
 *
 * <p>See: https://bugs.mojang.com/browse/MC-149777
 *
 */
@Mixin(StructureManager.class)
public abstract class StructureManagerMixin {
	@Shadow
	@Final
	@Mutable
	private Map<Identifier, Structure> structures;

	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void init(CallbackInfo info) {
		structures = Collections.synchronizedMap(structures);
	}
}
