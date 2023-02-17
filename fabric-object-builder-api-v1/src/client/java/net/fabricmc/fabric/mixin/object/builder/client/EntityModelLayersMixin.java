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

package net.fabricmc.fabric.mixin.object.builder.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.WoodType;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

@Mixin(EntityModelLayers.class)
public class EntityModelLayersMixin {
	@Inject(method = "createSign", at = @At("HEAD"), cancellable = true)
	private static void createSign(WoodType type, CallbackInfoReturnable<EntityModelLayer> cir) {
		if (type.name().indexOf(Identifier.NAMESPACE_SEPARATOR) != -1) {
			Identifier identifier = new Identifier(type.name());
			cir.setReturnValue(new EntityModelLayer(new Identifier(identifier.getNamespace(), "sign/" + identifier.getPath()), "main"));
		}
	}

	@Inject(method = "createHangingSign", at = @At("HEAD"), cancellable = true)
	private static void createHangingSign(WoodType type, CallbackInfoReturnable<EntityModelLayer> cir) {
		if (type.name().indexOf(Identifier.NAMESPACE_SEPARATOR) != -1) {
			Identifier identifier = new Identifier(type.name());
			cir.setReturnValue(new EntityModelLayer(new Identifier(identifier.getNamespace(), "hanging_sign/" + identifier.getPath()), "main"));
		}
	}
}
