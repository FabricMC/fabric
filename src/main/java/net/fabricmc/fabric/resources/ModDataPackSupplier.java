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

import net.minecraft.class_3285;
import net.minecraft.class_3288;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModDataPackSupplier implements class_3285 {
	@Override
	public <T extends class_3288> void method_14453(Map<String, T> map, class_3288.class_3290<T> class_3290) {
		// TODO: "vanilla" does not emit a message; neither should a modded datapack
		List<ResourcePack> packs = new ArrayList<>();
		ModResourcePackUtil.appendModResourcePacks(packs, ResourceType.DATA);
		for (ResourcePack pack : packs) {
			if (!(pack instanceof ModResourcePack)) {
				throw new RuntimeException("Not a ModResourcePack!");
			}

			T var3 = class_3288.method_14456("fabric/" + ((ModResourcePack) pack).getModInfo().getId(),
				false, () -> pack, class_3290, class_3288.class_3289.BOTTOM);

			if (var3 != null) {
				map.put(var3.method_14463(), var3);
			}
		}
	}
}
