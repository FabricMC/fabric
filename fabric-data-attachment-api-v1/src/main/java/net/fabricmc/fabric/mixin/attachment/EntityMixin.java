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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;
import net.fabricmc.fabric.impl.attachment.AttachmentTypeImpl;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentSync;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentSyncPredicateImpl;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentTargetInfo;
import net.fabricmc.fabric.impl.attachment.sync.s2c.AttachmentSyncPayload;

@Mixin(Entity.class)
abstract class EntityMixin implements AttachmentTargetImpl {
	@Shadow
	private int id;

	@Shadow
	public abstract World getWorld();

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
	public AttachmentTargetInfo<?> fabric_getSyncTargetInfo() {
		return new AttachmentTargetInfo.EntityTarget(this.id);
	}

	@Override
	public void fabric_syncChange(AttachmentType<?> type, AttachmentSyncPayload payload) {
		if (!this.getWorld().isClient) {
			AttachmentSyncPredicateImpl pred = ((AttachmentTypeImpl<?>) type).syncPredicate();
			assert pred != null;

			switch (pred.type()) {
			case ALL -> PlayerLookup
					.tracking((Entity) (Object) this)
					.forEach(player -> AttachmentSync.trySync(payload, player));
			case ALL_BUT_TARGET -> PlayerLookup
					.tracking((Entity) (Object) this)
					.forEach(player -> {
						if (player != (Object) this) {
							AttachmentSync.trySync(payload, player);
						}
					});
			case TARGET_ONLY -> {
				if ((Object) this instanceof ServerPlayerEntity player) {
					AttachmentSync.trySync(payload, player);
				}
			}
			case CUSTOM -> PlayerLookup
					.tracking((Entity) (Object) this)
					.forEach(player -> {
						if (pred.customTest().test(this, player)) {
							AttachmentSync.trySync(payload, player);
						}
					});
			}
		}
	}
}
