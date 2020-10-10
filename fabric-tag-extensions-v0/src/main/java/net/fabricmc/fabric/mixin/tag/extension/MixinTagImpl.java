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

package net.fabricmc.fabric.mixin.tag.extension;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.tag.Tag;
import net.minecraft.tag.SetTag;

import net.fabricmc.fabric.api.tag.FabricTag;
import net.fabricmc.fabric.impl.tag.extension.FabricTagHooks;

@Mixin(value = {SetTag.class}, targets = {"net.minecraft.tag.Tag$1", "net.minecraft.tag.RequiredTagList$TagWrapper"})
public abstract class MixinTagImpl<T> implements FabricTag<T>, FabricTagHooks, Tag<T> {
	@Unique
	private int fabric_clearCount;

	@Override
	public boolean hasBeenReplaced() {
		return fabric_clearCount > 0;
	}

	@Override
	public void fabric_setExtraData(int clearCount) {
		this.fabric_clearCount = clearCount;
	}
}
