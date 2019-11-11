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

package net.fabricmc.fabric.impl.datafixer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.mojang.datafixers.DataFixer;

import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TagHelper;

import net.fabricmc.fabric.impl.datafixer.FabricDataFixerImpl;

@Mixin(TagHelper.class)
public class MixinTagHelper {
	@Inject(at = @At("RETURN"), method = "update(Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/datafixers/DataFixTypes;Lnet/minecraft/nbt/CompoundTag;II)Lnet/minecraft/nbt/CompoundTag;", cancellable = true)
	private static void updateModFixers(DataFixer vanillaDataFixer, DataFixTypes dataFixTypes, CompoundTag inputTag$unusued, int vanillaDynamicDataVersion, int vanillaRuntimeDataVersion, CallbackInfoReturnable<CompoundTag> cir) {
		CompoundTag original = cir.getReturnValue(); // We do our fixes after vanilla.
		CompoundTag finalTag = FabricDataFixerImpl.INSTANCE.updateWithAllFixers(dataFixTypes, original);
		cir.setReturnValue(finalTag);
	}
}
