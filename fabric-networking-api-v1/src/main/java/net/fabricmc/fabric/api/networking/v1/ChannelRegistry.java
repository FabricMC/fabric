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

package net.fabricmc.fabric.api.networking.v1;

import java.util.Collection;

import net.minecraft.util.Identifier;

/**
 * Represents an object that keeps track of a collection of supported channels, for sending or reception purposes.
 */
public interface ChannelRegistry {
	/**
	 * Returns a collection of channels this registry supports.
	 *
	 * <p>This collection does not contain duplicate channels.</p>
	 *
	 * @return a collection of channels
	 */
	Collection<Identifier> getChannels();

	/**
	 * Returns whether a channel is supported by this registry.
	 *
	 * @param channel the id of the channel to check
	 * @return whether the channel is tracked
	 */
	boolean hasChannel(Identifier channel);
}
