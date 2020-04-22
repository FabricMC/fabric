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

import java.util.List;
import java.util.Optional;

import com.google.gson.JsonObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.tag.Tag;

import net.fabricmc.fabric.api.tag.FabricTagBuilder;
import net.fabricmc.fabric.impl.tag.extension.FabricTagHooks;

@Mixin(Tag.Builder.class)
public class MixinTagBuilder<T> implements FabricTagBuilder<T> {
	@Shadow
	private List<Tag.TrackedEntry> entries;

	@Unique
	private int fabric_clearCount;

	@Redirect(method = "build", at = @At(value = "INVOKE", target = "Ljava/util/Optional;of(Ljava/lang/Object;)Ljava/util/Optional;"))
	private Optional<?> build(Object tagObj) {
		((FabricTagHooks) tagObj).fabric_setExtraData(fabric_clearCount);
		return Optional.of(tagObj);
	}

	@Inject(at = @At(value = "INVOKE", target = "Ljava/util/List;clear()V"), method = "read")
	public void onFromJsonClear(JsonObject json, String packName, CallbackInfoReturnable<Tag.Builder> info) {
		fabric_clearCount++;
	}

	@Override
	public void clearTagEntries() {
		entries.clear();
		fabric_clearCount++;
	}
}
