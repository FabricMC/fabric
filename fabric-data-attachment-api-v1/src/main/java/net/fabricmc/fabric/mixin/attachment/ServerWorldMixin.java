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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import net.fabricmc.fabric.impl.attachment.AttachmentPersistentState;

@Mixin(ServerWorld.class)
abstract class ServerWorldMixin {
	@Inject(at = @At("TAIL"), method = "<init>")
	private void createAttachmentsPersistentState(CallbackInfo ci) {
		// Force persistent state creation
		ServerWorld world = (ServerWorld) (Object) this;
		var type = new PersistentState.Type<>(
				() -> new AttachmentPersistentState(world),
				nbt -> AttachmentPersistentState.read(world, nbt),
				null // Object builder API 12.1.0 and later makes this a no-op
		);
		world.getPersistentStateManager().getOrCreate(type, AttachmentPersistentState.ID);
	}
}
