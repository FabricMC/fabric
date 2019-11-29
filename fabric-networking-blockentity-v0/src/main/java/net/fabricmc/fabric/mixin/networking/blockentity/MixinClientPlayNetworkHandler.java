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

package net.fabricmc.fabric.mixin.networking.blockentity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
	@Unique
	private static Logger FABRIC_LOGGER = LogManager.getLogger();

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/packet/BlockEntityUpdateS2CPacket;getBlockEntityType()I", ordinal = 0), method = "onBlockEntityUpdate", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet, CallbackInfo info, BlockEntity entity) {
		if (entity instanceof BlockEntityClientSerializable) {
			if (packet.getBlockEntityType() == 127) {
				BlockEntityClientSerializable serializable = (BlockEntityClientSerializable) entity;
				String id = packet.getCompoundTag().getString("id");

				if (id != null) {
					Identifier otherIdObj = BlockEntityType.getId(entity.getType());

					if (otherIdObj == null) {
						FABRIC_LOGGER.error(entity.getClass() + " is missing a mapping! This is a bug!");
						info.cancel();
						return;
					}

					String otherId = otherIdObj.toString();

					if (otherId.equals(id)) {
						serializable.fromClientTag(packet.getCompoundTag());
					}
				}
			}

			info.cancel();
		}
	}

	@Redirect(method = "onChunkData", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BlockEntity;fromTag(Lnet/minecraft/nbt/CompoundTag;)V"))
	public void deserializeBlockEntityChunkData(BlockEntity entity, CompoundTag tag) {
		if (entity instanceof BlockEntityClientSerializable) {
			((BlockEntityClientSerializable) entity).fromClientTag(tag);
		} else {
			entity.fromTag(tag);
		}
	}
}
