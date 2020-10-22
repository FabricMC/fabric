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

import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.TridentItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * This is the default implementation for FabricTrident, allowing for the easy creation of new tridents with no new modded functionality.
 */
public class SimpleTridentItem extends TridentItem implements TridentInterface {
	private static final Identifier DEFAULT_TEXTURE = TridentEntityModel.TEXTURE;

	private final Identifier tridentEntityIdentifier;

	public SimpleTridentItem(Settings settings) {
		this(settings, DEFAULT_TEXTURE);
	}

	public SimpleTridentItem(Settings settings, Identifier tridentEntityTexture) {
		super(settings);
		this.tridentEntityIdentifier = tridentEntityTexture;
	}

	@Override
	public ModelIdentifier getInventoryModelIdentifier() {
		// super hacky, probably a better way but it works
		return new ModelIdentifier(Registry.ITEM.getId(this).toString() + "#inventory");
	}

	@Override
	public Identifier getEntityTexture() {
		return this.tridentEntityIdentifier;
	}

	@Override
	public TridentEntity getTridentEntity(TridentEntity trident) {
		return new SimpleTridentItemEntity(trident);
	}
}
