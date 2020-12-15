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

/**
 * The Resource Loader, version 0.
 *
 * <p><h3>Quick note about vocabulary in Resource Loader and Minecraft:</h3>
 * <ul>
 *  <li>Resource Pack refers to both client-sided resource pack and data pack.</li>
 *  <li>Virtual Resource Pack refers to a resource pack that may be generated at runtime, or simply doesn't exist directly on disk.</li>
 *  <li>Group Resource Pack refers to a virtual resource pack that groups multiple resource packs together.</li>
 * </ul>
 * </p>
 *
 * <p><h3>Modded Resource Pack Handling</h3></p>
 * <p>The Resource Loader will create a resource pack for each mod that provides resources in {@code assets} or {@code data}
 * sub-directories.
 * Those mod resource packs are grouped into a single always-enabled group resource pack which is shown in the resource pack screen.</p>
 *
 * <p><h4>Built-in Mod Resource Pack</h4></p>
 * <p>The Resource Loader adds manually registered mod resource packs. Those resource packs are registered with
 * {@link net.fabricmc.fabric.api.resource.ResourceManagerHelper#registerBuiltinResourcePack(net.minecraft.util.Identifier, net.fabricmc.loader.api.ModContainer, net.fabricmc.fabric.api.resource.ResourcePackActivationType)}</p>
 *
 * <p><h4>Programmer Art Resource Pack</h4></p>
 * <p>The Resource Loader will inject resources into the Programmer Art resource pack for each mod that provides
 * Programmer Art resources in the {@code programmer_art} top-level directory of the mod
 * whose structure is similar to a normal resource pack.</p>
 *
 * <p><h3>Resource Reload Listener</h3></p>
 * <p>The Resource Loader allows mods to register resource reload listeners through
 * {@link net.fabricmc.fabric.api.resource.ResourceManagerHelper#registerReloadListener(net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener)},
 * which are triggered when resources are reloaded.
 * A resource reload listener can depend on another and vanilla resource reload listener identifiers may be found in {@link net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys}.</p>
 */

package net.fabricmc.fabric.api.resource;
