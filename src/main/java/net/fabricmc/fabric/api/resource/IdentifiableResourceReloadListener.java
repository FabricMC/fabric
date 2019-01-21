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

package net.fabricmc.fabric.api.resource;

import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Collections;

/**
 * Interface for "identifiable" resource reload listeners.
 *
 * "Identifiable" listeners have an unique identifier, which can be depended on,
 * and can provide dependencies that they would like to see executed before
 * themselves.
 *
 * {@link ResourceReloadListenerKeys}
 */
public interface IdentifiableResourceReloadListener extends ResourceReloadListener {
	/**
	 * @return The unique identifier of this listener.
	 */
	Identifier getFabricId();

	/**
	 * @return The identifiers of listeners this listener expects to have been
	 * executed before itself.
	 */
	default Collection<Identifier> getFabricDependencies() {
		return Collections.emptyList();
	}

	/**
	 * By default, resource reload listeners in Minecraft are all executed on
	 * the game's main thread while the game is paused. This means they do not
	 * need to provide any guarantees regarding their thread safety, or usage
	 * of resources potentially modified by other reload listeners.
	 *
	 * "Thread safety", in this context, refers simply to independence; namely
	 * whether or not, under the condition that all of its dependencies have
	 * already been processed, this resource reload listener can run without
	 * accessing or modifying areas it does not control in a non-thread-safe
	 * manner.
	 *
	 * @return Whether or not the listener can be executed in a thread-safe way.
	 */
	default boolean isListenerThreadSafe() {
		return false;
	}
}
