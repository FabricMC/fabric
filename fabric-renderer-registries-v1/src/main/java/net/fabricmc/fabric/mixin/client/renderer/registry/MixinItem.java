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

package net.fabricmc.fabric.mixin.client.renderer.registry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.item.Item;

import net.fabricmc.fabric.api.client.rendereregistry.v1.item.CooldownOverlayProperties;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.CountLabelProperties;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.CustomItemOverlayRenderer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.DurabilityBarProperties;
import net.fabricmc.fabric.impl.client.renderer.registry.ItemOverlayExtensions;

@Mixin(Item.class)
public abstract class MixinItem implements ItemOverlayExtensions {
	@Unique private CountLabelProperties clp = CountLabelProperties.DEFAULT;
	@Unique private DurabilityBarProperties dbp = DurabilityBarProperties.DEFAULT;
	@Unique private CooldownOverlayProperties cop = CooldownOverlayProperties.DEFAULT;
	@Unique private CustomItemOverlayRenderer cior;

	@Override
	public CountLabelProperties fabric_getCountLabelProperties() {
		return clp;
	}

	@Override
	public void fabric_setCountLabelProperties(CountLabelProperties clp) {
		this.clp = clp;
	}

	@Override
	public DurabilityBarProperties fabric_getDurabilityBarProperties() {
		return dbp;
	}

	@Override
	public void fabric_setDurabilityBarProperties(DurabilityBarProperties dbp) {
		this.dbp = dbp;
	}

	@Override
	public CooldownOverlayProperties fabric_getCooldownOverlayProperties() {
		return cop;
	}

	@Override
	public void fabric_setCooldownOverlayProperties(CooldownOverlayProperties cop) {
		this.cop = cop;
	}

	@Override
	public CustomItemOverlayRenderer fabric_getCustomItemOverlayRenderer() {
		return cior;
	}

	@Override
	public void fabric_setCustomOverlayRenderer(CustomItemOverlayRenderer cior) {
		this.cior = cior;
	}
}
