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

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentChange;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentSync;
import net.fabricmc.fabric.impl.attachment.sync.EntityAttachmentChangePayloadS2C;

@Mixin(Entity.class)
abstract class EntityMixin implements AttachmentTargetImpl {
	@Shadow
	public abstract World getWorld();

	@Shadow
	private int id;

	@Inject(
			at = @At(value = "INVOKE", target = "net/minecraft/entity/Entity.readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V"),
			method = "readNbt"
	)
	private void readEntityAttachments(NbtCompound nbt, CallbackInfo cir) {
		this.fabric_readAttachmentsFromNbt(nbt, getWorld().getRegistryManager());
	}

	@Inject(
			at = @At(value = "INVOKE", target = "net/minecraft/entity/Entity.writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V"),
			method = "writeNbt"
	)
	private void writeEntityAttachments(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
		this.fabric_writeAttachmentsToNbt(nbt, getWorld().getRegistryManager());
	}

	@Override
	public void fabric_syncChange(AttachmentType<?> type, Object change) {
		var payload = new EntityAttachmentChangePayloadS2C(
				this.id,
				List.of(new AttachmentChange(type, change))
		);

		PlayerLookup.tracking((Entity) (Object) this)
				.forEach(player -> AttachmentSync.syncIfPossible(payload, type, this, player));
	}
}
