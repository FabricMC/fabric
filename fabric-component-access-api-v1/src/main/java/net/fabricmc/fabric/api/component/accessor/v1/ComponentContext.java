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

package net.fabricmc.fabric.api.component.accessor.v1;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Data object that carries information to components regarding
 * their state and the conditions in which they are being accessed.
 */
public interface ComponentContext {
	/**
	 * Identifies a requested location or sub-component within the provider,
	 * or {@code null} if the access request did not specify.
	 * Component providers are not required to supports this feature.
	 *
	 * @return Identifier of a requested location or sub-component within the provider
	 */
	/* @Nullable */ Identifier id();

	/**
	 * The side from which access to the component was requested,
	 * or {@code null} if the access request did not specify a side.
	 *
	 * @return The side from which access to the component was requested
	 */
	/* @Nullable */ Direction side();

	/**
	 * The server-side game world in which the access request was made,
	 * and in which the component is located.
	 *
	 * @return The server-side game world in which the component is located
	 */
	World world();
}
