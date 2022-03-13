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

package net.fabricmc.fabric.mixin.fluid;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import net.fabricmc.fabric.api.fluid.v1.FabricFluid;

@Mixin(Fluid.class)
public class MixinFluid implements FabricFluid {
	@Override
	public Optional<SoundEvent> getFabricBucketEmptySound() {
		// Default behaviour (from playEmptyingSound method in BucketItem).
		//noinspection ConstantConditions
		return Optional.of(((Object) this) instanceof LavaFluid ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY);
	}
}
