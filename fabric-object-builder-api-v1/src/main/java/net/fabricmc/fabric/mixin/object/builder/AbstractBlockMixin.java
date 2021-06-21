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

package net.fabricmc.fabric.mixin.object.builder;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;

import net.fabricmc.fabric.impl.object.builder.AbstractBlockInternals;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin implements AbstractBlockInternals {
	@Shadow
	@Final
	protected Material material;
	@Nullable
	private PistonBehavior pistonBehavior;
	@Nullable
	private Boolean replaceable;
	@Nullable
	private Boolean solid;

	@Override
	public void setPistonBehavior(PistonBehavior pistonBehavior) {
		this.pistonBehavior = pistonBehavior;
	}

	@Inject(method = "getPistonBehavior(Lnet/minecraft/block/BlockState;)Lnet/minecraft/block/piston/PistonBehavior;", at = @At("RETURN"), cancellable = true)
	public void pistonBehaviorInject(BlockState state, CallbackInfoReturnable<PistonBehavior> cir) {
		if (this.pistonBehavior != null) {
			cir.setReturnValue(this.pistonBehavior);
		}
	}

	@Override
	public boolean isReplaceable(BlockState state) {
		if (replaceable != null) {
			return replaceable;
		}

		return this.material.isReplaceable();
	}

	@Override
	public void setReplaceable(boolean replaceable) {
		this.replaceable = replaceable;
	}

	@Override
	public boolean isSolid(BlockState state) {
		if (solid != null) {
			return solid;
		}

		return this.material.isSolid();
	}

	@Override
	public void setSolid(boolean solid) {
		this.solid = solid;
	}

	@Inject(method = "canReplace(Lnet/minecraft/block/BlockState;Lnet/minecraft/item/ItemPlacementContext;)Z", at = @At("RETURN"), cancellable = true)
	public void canReplaceInject(BlockState state, ItemPlacementContext context, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(this.isReplaceable(state) && (context.getStack().isEmpty() || context.getStack().getItem() != this.asItem()));
	}

	@Inject(method = "canBucketPlace(Lnet/minecraft/block/BlockState;Lnet/minecraft/fluid/Fluid;)Z", at = @At("RETURN"), cancellable = true)
	public void canBucketPlaceInject(BlockState state, Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(this.isReplaceable(state) || !this.isSolid(state));
	}

	@Shadow
	public abstract Item asItem();
}
