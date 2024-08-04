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

package net.fabricmc.fabric.mixin.client.rendering.shader;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.resource.ResourceFactory;

import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.impl.client.rendering.FabricShaderProgram;

/**
 * Implements custom core shader registration (CoreShaderRegistrationCallback).
 */
@Mixin(GameRenderer.class)
abstract class GameRendererMixin {
	@Inject(
			method = "loadPrograms",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;loadBlurPostProcessor(Lnet/minecraft/resource/ResourceFactory;)V")
	)
	private void registerShaders(ResourceFactory factory, CallbackInfo info, @Local(ordinal = 0) List<?> shaderStages, @Local(ordinal = 1) List<Pair<ShaderProgram, Consumer<ShaderProgram>>> programs) throws IOException {
		CoreShaderRegistrationCallback.RegistrationContext context = (id, vertexFormat, loadCallback) -> {
			ShaderProgram program = new FabricShaderProgram(factory, id, vertexFormat);
			programs.add(Pair.of(program, loadCallback));
		};
		CoreShaderRegistrationCallback.EVENT.invoker().registerShaders(context);
	}
}
