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

import java.util.List;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.DataFixerUpper;
import com.mojang.datafixers.schemas.Schema;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.fabric.api.datafixer.v1.FabricDataFixerBuilder;
import net.fabricmc.fabric.api.datafixer.v1.FabricDataFixerUpper;

@Mixin(value = DataFixerBuilder.class, remap = false)
public class DataFixerBuilderMixin {
	@Shadow
	@Final
	private Int2ObjectSortedMap<Schema> schemas;

	@Shadow
	@Final
	private List<DataFix> globalList;

	@Shadow
	@Final
	private IntSortedSet fixerVersions;

	@Inject(method = "build", at = @At("HEAD"), cancellable = true, remap = false)
	private void buildFabric(CallbackInfoReturnable<DataFixerUpper> cir) {
		DataFixerBuilder builder = DataFixerBuilder.class.cast(this);

		if (builder instanceof FabricDataFixerBuilder) {
			cir.setReturnValue(new FabricDataFixerUpper(this.schemas, this.globalList, this.fixerVersions));
		}
	}
}
