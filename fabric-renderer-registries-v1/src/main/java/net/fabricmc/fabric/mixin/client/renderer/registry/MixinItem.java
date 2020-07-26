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
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.PostItemOverlayRenderer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.PreItemOverlayRenderer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.DurabilityBarProperties;
import net.fabricmc.fabric.impl.client.renderer.registry.item.ItemOverlayExtensions;

@Mixin(Item.class)
public abstract class MixinItem implements ItemOverlayExtensions {
	@Unique private CountLabelProperties countLabelProperties = CountLabelProperties.DEFAULT;
	@Unique private DurabilityBarProperties durabilityBarProperties = DurabilityBarProperties.DEFAULT;
	@Unique private CooldownOverlayProperties cooldownOverlayProperties = CooldownOverlayProperties.DEFAULT;
	@Unique private PreItemOverlayRenderer preItemOverlayRenderer = PreItemOverlayRenderer.DEFAULT;
	@Unique private PostItemOverlayRenderer postItemOverlayRenderer = PostItemOverlayRenderer.DEFAULT;

	@Override
	public CountLabelProperties fabric_getCountLabelProperties() {
		return countLabelProperties;
	}

	@Override
	public void fabric_setCountLabelProperties(CountLabelProperties clp) {
		this.countLabelProperties = clp;
	}

	@Override
	public DurabilityBarProperties fabric_getDurabilityBarProperties() {
		return durabilityBarProperties;
	}

	@Override
	public void fabric_setDurabilityBarProperties(DurabilityBarProperties dbp) {
		this.durabilityBarProperties = dbp;
	}

	@Override
	public CooldownOverlayProperties fabric_getCooldownOverlayProperties() {
		return cooldownOverlayProperties;
	}

	@Override
	public void fabric_setCooldownOverlayProperties(CooldownOverlayProperties cop) {
		this.cooldownOverlayProperties = cop;
	}

	@Override
	public PreItemOverlayRenderer fabric_getPreItemOverlayRenderer() {
		return preItemOverlayRenderer;
	}

	@Override
	public void fabric_setPreOverlayRenderer(PreItemOverlayRenderer pior) {
		this.preItemOverlayRenderer = pior;
	}

	@Override
	public PostItemOverlayRenderer fabric_getPostItemOveralyRenderer() {
		return postItemOverlayRenderer;
	}

	@Override
	public void fabric_setPostOverlayRenderer(PostItemOverlayRenderer pior) {
		this.postItemOverlayRenderer = pior;
	}
}
