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

package net.fabricmc.fabric.impl.resource.conditions.conditions;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.dynamic.Codecs;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.impl.resource.conditions.DefaultResourceConditionTypes;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;

public record OrResourceCondition(List<ResourceCondition> conditions) implements ResourceCondition {
	public static final MapCodec<OrResourceCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codecs.nonEmptyList(ResourceCondition.CODEC.listOf()).fieldOf("values").forGetter(OrResourceCondition::conditions)
	).apply(instance, OrResourceCondition::new));

	@Override
	public ResourceConditionType<?> getType() {
		return DefaultResourceConditionTypes.OR;
	}

	@Override
	public boolean test() {
		return ResourceConditionsImpl.conditionsMet(this.conditions(), false);
	}
}
