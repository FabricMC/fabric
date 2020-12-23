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

package net.fabricmc.fabric.mixin.tag.extension;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.tag.FabricDataGeneratorTagBuilder;

@Mixin(AbstractTagProvider.ObjectBuilder.class)
public class MixinObjectBuilder<T> implements FabricDataGeneratorTagBuilder<T> {
	@Shadow
	@Final private Tag.Builder field_23960;

	@Shadow
	@Final private String field_23962;

	@Override
	public void addOptionalObject(Identifier id) {
		field_23960.add(new Tag.OptionalObjectEntry(id), field_23962);
	}

	@Override
	public void addOptionalTag(Identifier id) {
		field_23960.add(new Tag.OptionalTagEntry(id), field_23962);
	}
}
