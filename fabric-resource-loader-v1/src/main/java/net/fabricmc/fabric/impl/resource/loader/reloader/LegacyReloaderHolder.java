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

package net.fabricmc.fabric.impl.resource.loader.reloader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;

/**
 * Support for registration of resource reloaders using the legacy v0 API.
 */
public class LegacyReloaderHolder {
	public static final List<Definition> CLIENT_DEFINITIONS = new ArrayList<>();
	public static final List<Definition> SERVER_DEFINITIONS = new ArrayList<>();

	public record Definition(ResourceReloader reloader, Supplier<Identifier> idSupplier, Supplier<Collection<Identifier>> dependencySupplier) {
	}
}
