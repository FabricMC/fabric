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

package net.fabricmc.fabric.impl.client.texture;

import net.minecraft.class_1050;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

public class FabricSprite extends Sprite {
	public FabricSprite(Identifier var1, int var2, int var3) {
		super(var1, var2, var3);
	}

	public FabricSprite(Identifier identifier, class_1050 class_1050, AnimationResourceMetadata animationResourceMetadata) {
		super(identifier, class_1050, animationResourceMetadata);
	}
}
