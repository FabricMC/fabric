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

package net.fabricmc.fabric.mixin.networking;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketEncoder;

import net.fabricmc.fabric.impl.networking.vanilla.PacketEncoderExtensions;

@Mixin(PacketEncoder.class)
abstract class PacketEncoderMixin implements PacketEncoderExtensions {
	@Shadow
	protected abstract void encode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, ByteBuf byteBuf) throws Exception;

	@Unique
	private boolean suppressSizeError;

	@Unique
	private boolean success;

	@Inject(method = "encode(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;Lio/netty/buffer/ByteBuf;)V",
			at = @At(value = "NEW", target = "java/lang/IllegalArgumentException"), cancellable = true)
	private void suppressSizeError(ChannelHandlerContext channelHandlerContext, Packet<?> packet, ByteBuf byteBuf, CallbackInfo ci) {
		if (suppressSizeError) {
			success = false;
			ci.cancel();
		}
	}

	@Override
	public boolean fabric_tryEncode(ChannelHandlerContext ctx, Packet<?> packet, ByteBuf out) throws Exception {
		success = true;
		suppressSizeError = true;
		encode(ctx, packet, out);
		suppressSizeError = false;
		return success;
	}
}
