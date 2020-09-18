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

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.StructureFeature;

import net.fabricmc.fabric.impl.structure.FabricStructureUtil;
import net.fabricmc.fabric.impl.structure.StructuresConfigHooks;

@Mixin(StructuresConfig.class)
public class MixinStructuresConfig implements StructuresConfigHooks {
	@Shadow
	@Final
	private Map<StructureFeature<?>, StructureConfig> structures;

	// This constructor of StructuresConfig initializes it with the default set of structures.
	// Since a mod can register its structures later, we need to keep track of the object created
	// here, so that we can add new structures to it later.
	@Inject(method = "<init>(Z)V", at = @At("RETURN"))
	private void onDefaultInit(CallbackInfo ci) {
		FabricStructureUtil.DEFAULT_STRUCTURES_CONFIGS.add((StructuresConfig) (Object) this);
	}

	@Override
	public void fabric_updateDefaultEntries() {
		StructuresConfig.DEFAULT_STRUCTURES.forEach(structures::putIfAbsent);
	}
}
