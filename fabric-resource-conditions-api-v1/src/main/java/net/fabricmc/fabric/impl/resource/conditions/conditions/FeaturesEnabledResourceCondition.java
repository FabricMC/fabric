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

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.impl.resource.conditions.DefaultResourceConditionTypes;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;

public record FeaturesEnabledResourceCondition(FeatureSet features) implements ResourceCondition {
	public static final MapCodec<FeaturesEnabledResourceCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			FeatureFlags.CODEC.fieldOf("features").forGetter(FeaturesEnabledResourceCondition::features)
	).apply(instance, FeaturesEnabledResourceCondition::new));

	public FeaturesEnabledResourceCondition(FeatureFlag... features) {
		this(FeatureFlags.FEATURE_MANAGER.featureSetOf(features));
	}

	@Override
	public ResourceConditionType<?> getType() {
		return DefaultResourceConditionTypes.FEATURES_ENABLED;
	}

	@Override
	public boolean test(@Nullable RegistryWrapper.WrapperLookup registryLookup) {
		return ResourceConditionsImpl.featuresEnabled(this.features());
	}
}
