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

package net.fabricmc.fabric.impl.client.renderer.registry;

import net.fabricmc.fabric.api.client.rendereregistry.v1.item.CooldownOverlayProperties;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.CountLabelProperties;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.CustomItemOverlayRenderer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.DurabilityBarProperties;

public interface ItemOverlayExtensions {
	CountLabelProperties fabric_getCountLabelProperties();
	void fabric_setCountLabelProperties(CountLabelProperties clp);

	DurabilityBarProperties fabric_getDurabilityBarProperties();
	void fabric_setDurabilityBarProperties(DurabilityBarProperties dbp);

	CooldownOverlayProperties fabric_getCooldownOverlayProperties();
	void fabric_setCooldownOverlayProperties(CooldownOverlayProperties cop);

	CustomItemOverlayRenderer fabric_getCustomItemOverlayRenderer();
	void fabric_setCustomOverlayRenderer(CustomItemOverlayRenderer cior);
}
