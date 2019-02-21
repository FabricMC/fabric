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

package net.fabricmc.fabric.mixin.client.render;

import com.google.common.collect.Maps;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

public class MixinArmorFeatureRenderer {
	@Shadow
	private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.newHashMap();
	
	@Overwrite
	private Identifier method_4174(ArmorItem armorItem, boolean isSecond, String suffix) {
		Identifier identifier = new Identifier(armorItem.getMaterial().getName());
		String string_2 = identifier.getNamespace() + ":textures/models/armor/" + identifier.getPath() + "_layer_" + (isSecond ? 2 : 1) + (suffix == null ? "" : "_" + suffix) + ".png";
		return ARMOR_TEXTURE_CACHE.computeIfAbsent(string_2, Identifier::new);
	}

}
