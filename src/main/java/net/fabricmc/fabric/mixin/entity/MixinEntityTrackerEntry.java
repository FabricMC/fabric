/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.mixin.entity;

import net.fabricmc.fabric.entity.EntityTrackingRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.server.network.EntityTrackerEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityTrackerEntry.class)
public class MixinEntityTrackerEntry {
	@Shadow
	private Entity entity;

	@Inject(at = @At(value = "CONSTANT", args = {"stringValue=Don't know how to addReloadListener "}), method = "createSpawnPacket", cancellable = true)
	public void createSpawnPacket(CallbackInfoReturnable<Packet> info) {
		Packet packet = EntityTrackingRegistry.INSTANCE.createSpawnPacket(entity);
		if (packet != null) {
			info.setReturnValue(packet);
			info.cancel();
		}
	}
}
