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

package net.fabricmc.fabric.impl.datafixer.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.datafixer.FabricDataFixerImpl;
import net.minecraft.client.options.HotbarStorage;
import net.minecraft.nbt.CompoundTag;

@Environment(EnvType.CLIENT)
@Mixin(HotbarStorage.class)
/**
 * Only DataFixer reference done exclusively by client. This is implemented because it stores itemstacks within HotbarStorageEntry.
 * Fixing on load is covered by TagHelper
 */
public class MixinHotbarStorage {
	// Also add Mod DataVersions to save()
	@Inject(at = @At(value = "INVOKE", target = "net/minecraft/nbt/CompoundTag.putInt(Ljava/lang/String;I)V"), method = "save()V", locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	public void onSave(CallbackInfo ci, CompoundTag compoundTag_1) {
		FabricDataFixerImpl.INSTANCE.addFixerVersions(compoundTag_1);
	}
}
