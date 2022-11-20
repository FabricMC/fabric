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

package net.fabricmc.fabric.impl.item.client;

import net.minecraft.client.item.TooltipData;
import net.minecraft.client.gui.tooltip.TooltipComponent;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.impl.item.BundledTooltipData;

public class FabricItemApiImpl implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		TooltipComponentCallback.EVENT.register(maybe -> {
			if (maybe instanceof BundledTooltipData data) {
				return new BundledTooltipComponentImpl(data.list().stream().map(FabricItemApiImpl::getComponent).toList());
			}

			return null;
		});
	}

	private static TooltipComponent getComponent(TooltipData data) {
		TooltipComponent component = TooltipComponentCallback.EVENT.invoker().getComponent(data);

		if (component != null) {
			return component;
		}

		return TooltipComponent.of(data);
	}
}
