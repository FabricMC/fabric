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

package net.fabricmc.fabric.impl.attachment;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

public class AttachmentEntrypoint implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) ->
				AttachmentTargetImpl.copyOnRespawn(oldPlayer, newPlayer, !alive)
		);
		ServerEntityWorldChangeEvents.AFTER_ENTITY_CHANGE_WORLD.register(((originalEntity, newEntity, origin, destination) ->
				AttachmentTargetImpl.copyOnRespawn(originalEntity, newEntity, false))
		);
		// using the corresponding player event is unnecessary as no new instance is created
		ServerLivingEntityEvents.MOB_CONVERSION.register((previous, converted, keepEquipment) ->
				AttachmentTargetImpl.copyOnRespawn(previous, converted, true)
		);
	}
}
