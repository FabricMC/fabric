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

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;

@Mixin(BlockEntityUpdateS2CPacket.class)
public class BlockEntityUpdateS2CPacketMixin {
	/*
	 * Some BEs use their NBT data to sync with client. If nothing is done, that would always sync persistent attachments
	 * with client, which may be undesirable. To prevent this, we hook into create(BlockEntity) so it uses a getter that
	 * also removes attachments. Manual sync is still possible by using create(BlockEntity, Function).
	 */
	@ModifyArg(
			method = "create(Lnet/minecraft/block/entity/BlockEntity;)Lnet/minecraft/network/packet/s2c/play/BlockEntityUpdateS2CPacket;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/network/packet/s2c/play/BlockEntityUpdateS2CPacket;create(Lnet/minecraft/block/entity/BlockEntity;Ljava/util/function/Function;)Lnet/minecraft/network/packet/s2c/play/BlockEntityUpdateS2CPacket;"
			)
	)
	private static Function<BlockEntity, NbtCompound> stripPersistentAttachmentData(Function<BlockEntity, NbtCompound> getter) {
		return be -> {
			NbtCompound nbt = getter.apply(be);
			nbt.remove(AttachmentTarget.NBT_ATTACHMENT_KEY);
			return nbt;
		};
	}
}
