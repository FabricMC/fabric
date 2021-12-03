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

package net.fabricmc.fabric.mixin.datagen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.tag.Tag;

import net.fabricmc.fabric.impl.datagen.FabricTagBuilder;

/**
 * Extends Tag.Builder to support setting the replace field.
 */
@Mixin(Tag.Builder.class)
public class TagBuilderMixin implements FabricTagBuilder {
	@Unique
	private boolean replace = false;

	@Override
	public void fabric_setReplace(boolean replace) {
		this.replace = replace;
	}

	@ModifyArg(method = "toJson", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonObject;addProperty(Ljava/lang/String;Ljava/lang/Boolean;)V"), index = 1)
	public Boolean modifyReplace(Boolean replace) {
		return this.replace;
	}
}
