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

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;

import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;

@Mixin(MobEntity.class)
abstract class MobEntityMixin implements AttachmentTargetImpl {
	@Inject(
			method = "convertTo",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;discard()V")
	)
	private <T extends MobEntity> void copyAttachmentsOnConversion(
			EntityType<T> entityType,
			boolean keepEquipment,
			CallbackInfoReturnable<T> cir,
			@Local MobEntity converted
	) {
		// TODO: add this to event API and migrate to use the event
		// Counting conversion as a form of death here
		AttachmentTargetImpl.copyOnRespawn(this, (AttachmentTargetImpl) converted, false);
	}
}
