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

package net.fabricmc.fabric.mixin.event.lifecycle;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
	@Inject(
			method = "onPlayerConnect",
			at = @At(value = "INVOKE", target = "net/minecraft/network/packet/s2c/play/SynchronizeRecipesS2CPacket.<init>(Ljava/util/Collection;)V")
	)
	private void hookOnPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData arg, CallbackInfo ci) {
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.invoker().onSyncDataPackContents(player, true);
	}

	@Inject(
			method = "onDataPacksReloaded",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/common/SynchronizeTagsS2CPacket;<init>(Ljava/util/Map;)V")
	)
	private void hookOnDataPacksReloaded(CallbackInfo ci) {
		for (ServerPlayerEntity player : ((PlayerManager) (Object) this).getPlayerList()) {
			ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.invoker().onSyncDataPackContents(player, false);
		}
	}
}
