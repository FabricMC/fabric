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

package net.fabricmc.fabric.mixin.datafixer.v1;

import com.mojang.datafixers.DataFixerUpper;
import com.mojang.datafixers.schemas.Schema;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.fabricmc.fabric.impl.datafixer.v1.DataFixerUpperExtension;

@Mixin(DataFixerUpper.class)
public class DataFixerUpperMixin implements DataFixerUpperExtension {
	@Shadow
	@Final
	private Int2ObjectSortedMap<Schema> schemas;

	@Override
	public Int2ObjectSortedMap<Schema> fabric_getSchemas() {
		return this.schemas;
	}
}
