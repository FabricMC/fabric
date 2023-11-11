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

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.gl.Program;
import net.minecraft.client.render.Shader;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.client.rendering.FabricShader;

@Mixin(Shader.class)
abstract class ShaderMixin {
	@Shadow
	@Final
	private String name;

	// Allow loading FabricShaderPrograms from arbitrary namespaces.
	@ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Identifier;<init>(Ljava/lang/String;)V"), allow = 1)
	private String modifyProgramId(String id) {
		if ((Object) this instanceof FabricShader) {
			return FabricShader.rewriteAsId(id, name);
		}

		return id;
	}

	// Allow loading shader stages from arbitrary namespaces.
	@ModifyVariable(method = "loadProgram", at = @At("STORE"), ordinal = 1)
	private static String modifyStageId(String id, ResourceFactory factory, Program.Type type, String name) {
		if (name.contains(String.valueOf(Identifier.NAMESPACE_SEPARATOR))) {
			return FabricShader.rewriteAsId(id, name);
		}

		return id;
	}
}
