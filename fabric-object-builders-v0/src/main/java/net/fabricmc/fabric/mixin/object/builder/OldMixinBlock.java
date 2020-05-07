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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.block.AbstractBlock;

import net.fabricmc.fabric.api.event.registry.BlockConstructedCallback;

@Mixin(Block.class)
@Deprecated
public class OldMixinBlock {
	@Inject(method = "<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V", at = @At("RETURN"))
	public void init(AbstractBlock.Settings builder, CallbackInfo info) {
		BlockConstructedCallback.EVENT.invoker().building(builder, (Block) (Object) this);
	}
}
