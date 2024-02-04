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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;

import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;

@Mixin(BlockEntity.class)
abstract class BlockEntityMixin implements AttachmentTargetImpl {
	@Inject(
			method = "method_17897", // lambda body in BlockEntity#createFromNbt
			at = @At(value = "INVOKE", target = "net/minecraft/block/entity/BlockEntity.readNbt(Lnet/minecraft/nbt/NbtCompound;)V")
	)
	private static void readBlockEntityAttachments(NbtCompound nbt, String id, BlockEntity blockEntity, CallbackInfoReturnable<BlockEntity> cir) {
		((AttachmentTargetImpl) blockEntity).fabric_readAttachmentsFromNbt(nbt);
	}

	@Inject(
			method = "createNbt",
			at = @At("RETURN")
	)
	private void writeBlockEntityAttachments(CallbackInfoReturnable<NbtCompound> cir) {
		this.fabric_writeAttachmentsToNbt(cir.getReturnValue());
	}
}
