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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.client.rendering.FabricShader;

/**
 * Lets modded shaders {@code #moj_import} shaders from any namespace with the
 * {@code <>} syntax.
 */
@Mixin(targets = "net.minecraft.client.render.Shader$1")
abstract class ShaderImportProcessorMixin {
	@Unique
	private String capturedImport;

	@Inject(method = "loadImport", at = @At("HEAD"))
	private void captureImport(boolean inline, String name, CallbackInfoReturnable<String> info) {
		capturedImport = name;
	}

	@ModifyVariable(method = "loadImport", at = @At("STORE"), ordinal = 0, argsOnly = true)
	private String modifyImportId(String id, boolean inline) {
		if (!inline && capturedImport.contains(String.valueOf(Identifier.NAMESPACE_SEPARATOR))) {
			return FabricShader.rewriteAsId(id, capturedImport);
		}

		return id;
	}

	@Inject(method = "loadImport", at = @At("RETURN"))
	private void uncaptureImport(boolean inline, String name, CallbackInfoReturnable<String> info) {
		capturedImport = null;
	}
}
