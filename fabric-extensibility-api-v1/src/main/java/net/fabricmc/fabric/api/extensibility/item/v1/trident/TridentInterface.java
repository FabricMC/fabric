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

package net.fabricmc.fabric.api.extensibility.item.v1.trident;

import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.Identifier;

/**
 * An interface to implement for all custom tridents in fabric. <br>
 * Note: This is meant to be used on a TridentItem class, the functionality will not work otherwise.
 *
 * @see SimpleTridentItem
 */
public interface TridentInterface {
	/**
	 * should be `namespace:item_name#inventory`.
	 *
	 * @return the model identifier
	 */
	ModelIdentifier getInventoryModelIdentifier();

	/**
	 *
	 * @return The Identifier for the texture of the trident entity
	 */
	Identifier getEntityTexture();

	/**
	 * Modifies the trident entity for this trident item, allowing for custom tridents that have different features. <br>
	 * Look at {@link SimpleTridentItem#modifyTridentEntity} for an example of how to construct a new trident entity from a vanilla one.
	 * @param trident The vanilla trident to base custom trident of off
	 * @return The custom trident
	 */
	TridentEntity modifyTridentEntity(TridentEntity trident);
}
