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

package net.fabricmc.fabric.mixin.client.renderer.registry;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.class_5614;
import net.minecraft.class_5616;
import net.minecraft.block.entity.BlockEntityType;

import net.fabricmc.fabric.impl.client.renderer.registry.BlockEntityRendererRegistryImpl;

@Mixin(class_5616.class)
public abstract class MixinBlockEntityRenderers {
	@Shadow()
	@Final
	private static Map<BlockEntityType<?>, class_5614<?>> field_27752;

	@Inject(at = @At("RETURN"), method = "<clinit>*")
	private static void init(CallbackInfo ci) {
		BlockEntityRendererRegistryImpl.setup(((t, factory) -> field_27752.put(t, factory)));
	}
}
