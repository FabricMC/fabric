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

package net.fabricmc.fabric.mixin.attachment;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.nbt.NbtCompound;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;

@Mixin(BannerBlockEntity.class)
abstract class BannerBlockEntityMixin {
	@ModifyExpressionValue(method = "toInitialChunkDataNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BannerBlockEntity;createNbt(Lnet/minecraft/registry/RegistryWrapper$WrapperLookup;)Lnet/minecraft/nbt/NbtCompound;"))
	private NbtCompound removeAttachments(NbtCompound original) {
		original.remove(AttachmentTarget.NBT_ATTACHMENT_KEY);
		return original;
	}
}
