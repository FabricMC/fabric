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

package net.fabricmc.fabric.mixin.client.model.loading;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.client.model.loading.BlockStatesLoaderHooks;

@Mixin(BlockStatesLoader.class)
abstract class BlockStatesLoaderMixin implements BlockStatesLoaderHooks {
	@Unique
	@Nullable
	private LoadingOverride loadingOverride;

	@WrapWithCondition(method = "load()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/BlockStatesLoader;loadBlockStates(Lnet/minecraft/util/Identifier;Lnet/minecraft/state/StateManager;)V"))
	private boolean shouldDoVanillaLoading(BlockStatesLoader self, Identifier id, StateManager<Block, BlockState> stateManager, @Local Block block) {
		return loadingOverride == null || !loadingOverride.loadBlockStates(block, id);
	}

	@Override
	public void fabric_setLoadingOverride(LoadingOverride override) {
		loadingOverride = override;
	}
}
