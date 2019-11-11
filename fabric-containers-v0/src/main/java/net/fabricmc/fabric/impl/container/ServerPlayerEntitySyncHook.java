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

package net.fabricmc.fabric.impl.container;

/**
 * This is a interface that is present on a ServerPlayerEntity, it allows access to the sync id.
 */
public interface ServerPlayerEntitySyncHook {
	/**
	 * Gets and sets the new player sync id, and returns the new value.
	 *
	 * @return the new sync id of the player
	 */
	int fabric_incrementSyncId();
}
