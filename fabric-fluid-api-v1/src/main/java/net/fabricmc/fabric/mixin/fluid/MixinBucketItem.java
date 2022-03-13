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

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

@Mixin(BucketItem.class)
public class MixinBucketItem {
	@Shadow
	@Final
	private Fluid fluid;

	/**
	 * @author FabricMC
	 * @reason Makes the sound totally dependent from the fluid.
	 */
	@Overwrite
	public void playEmptyingSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos) {
		fluid.getFabricBucketEmptySound().ifPresent(sound -> world.playSound(player, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F));
		world.emitGameEvent(player, GameEvent.FLUID_PLACE, pos);
	}
}
