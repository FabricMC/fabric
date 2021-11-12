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

import net.fabricmc.fabric.api.fluid.v1.util.FluidUtils;
import net.minecraft.entity.ai.pathing.WaterPathNodeMaker;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WaterPathNodeMaker.class)
public class WaterPathNodeMakerMixin {
	@Redirect(method = "getDefaultNodeType", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/tag/Tag;)Z"))
	private boolean isInRedirect1(FluidState state, Tag<Fluid> tag) {
		//This adds the fabric fluids to the valid fluids that does not block entities AI movements
		return state.isIn(FluidTags.WATER) || FluidUtils.isFabricFluid(state);
	}

	@Redirect(method = "getNodeType(III)Lnet/minecraft/entity/ai/pathing/PathNodeType;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/tag/Tag;)Z"))
	private boolean isInRedirect2(FluidState state, Tag<Fluid> tag) {
		//This adds the fabric fluids to the valid fluids that does not block entities AI movements
		return state.isIn(FluidTags.WATER) || FluidUtils.isFabricFluid(state);
	}
}
