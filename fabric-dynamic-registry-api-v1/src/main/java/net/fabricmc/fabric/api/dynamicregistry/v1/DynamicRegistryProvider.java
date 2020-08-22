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

import java.util.List;

/**
 * An entrypoint for developers to modify custom dynamic registries.
 *
 * <p>Registries added to this list can be changed from within datapacks.</p>
 *
 * <p>In {@code fabric.mod.json}, the entrypoint is defined with the {@code dynamic-registry-provider} key.</p>
 *
 * <pre><code>
 * public class ExampleDynamicRegistryProvider implements DynamicRegistryProvider {
 * 	public void getDynamicRegistries(List&lt;CustomDynamicRegistry&lt;?&gt;&gt; entries) {
 * 		entries.add(new CustomDynamicRegistry&lt;&gt;(CUSTOM_REGISTRY, () -> DEFAULT_VALUE, RegistryItem.CODEC));
 * 	}
 * }
 * </code></pre>
 *
 * @see net.fabricmc.loader.api.FabricLoader#getEntrypointContainers(String, Class)
 */
public interface DynamicRegistryProvider {
	void getDynamicRegistries(List<CustomDynamicRegistry<?>> entries);
}
