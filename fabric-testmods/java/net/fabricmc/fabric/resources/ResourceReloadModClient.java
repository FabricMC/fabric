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

package net.fabricmc.fabric.resources;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Collections;

public class ResourceReloadModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		for (int i = 64; i >= 2; i--) {
			final int _i = i;
			ResourceManagerHelper.get(ResourceType.ASSETS).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
				@Override
				public void apply(ResourceManager var1) {
					System.out.println("Reloading (should run as #" + _i + ")");
				}

				@Override
				public Identifier getFabricId() {
					return new Identifier("fabric:rrmc" + _i);
				}

				@Override
				public Collection<Identifier> getFabricDependencies() {
					return Collections.singletonList(new Identifier("fabric:rrmc" + (_i - 1)));
				}
			});
		}

		ResourceManagerHelper.get(ResourceType.ASSETS).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return new Identifier("fabric:rrmc1");
			}

			@Override
			public void apply(ResourceManager var1) {
				System.out.println("Reloading (should run as #1)");
			}
		});

		ResourceManagerHelper.get(ResourceType.ASSETS).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return new Identifier("fabric:rrmc_should_not_resolve");
			}

			@Override
			public Collection<Identifier> getFabricDependencies() {
				return Collections.singletonList(new Identifier("fabric:rrmc_nonexistent"));
			}

			@Override
			public void apply(ResourceManager var1) {
			}
		});
	}
}
