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

package net.fabricmc.fabric.api.dynamicregistry.v1;

import java.util.function.Consumer;

/**
 * An entrypoint for developers to modify custom dynamic registries.
 *
 * <p>A dynamic registry is an optional per-server extension to an existing registry. They are decoded from datapack JSONs at the path {@code data/&lt;entry_namespace&gt;/&lt;registry_path&gt;/&lt;entry_id&gt;.json} and synced to clients using a codec provided for registry entries.</p>
 *
 * <p>In {@code fabric.mod.json}, the entrypoint is defined with the {@code dynamic-registry-provider} key.</p>
 *
 * <p>Dynamic registries are initialized at different times in relation to the mod's initialization depending on the environment and version of Fabric loader. Due to this, some parts of the game may not be initialized yet. To prevent issues from occurring, the dynamic registry provider should be defined within a separate class and access as few other classes as possible.</p>
 *
 * <pre><code>
 * public class ExampleDynamicRegistryProvider implements DynamicRegistryProvider {
 * 	public void addDynamicRegistries(Consumer&lt;CustomDynamicRegistry&lt;?&gt;&gt; adder) {
 * 		adder.accept(new CustomDynamicRegistry&lt;&gt;(CUSTOM_REGISTRY, () -> DEFAULT_VALUE, RegistryItem.CODEC));
 * 	}
 * }
 * </code></pre>
 *
 * @see net.fabricmc.loader.api.FabricLoader#getEntrypointContainers(String, Class)
 */
public interface DynamicRegistryProvider {
	void addDynamicRegistries(Consumer<CustomDynamicRegistry<?>> adder);
}
