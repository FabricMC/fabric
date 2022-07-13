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

package net.fabricmc.fabric.mixin.resource.loader;

import com.google.gson.JsonObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.PackResourceMetadataReader;
import net.minecraft.util.JsonHelper;

import net.fabricmc.fabric.impl.resource.loader.FabricPackResourceMetadata;

@Mixin(PackResourceMetadataReader.class)
public class PackResourceMetadataReaderMixin {
	@Inject(method = "fromJson(Lcom/google/gson/JsonObject;)Lnet/minecraft/resource/metadata/PackResourceMetadata;", at = @At("RETURN"))
	private void readFabricPackFormat(JsonObject json, CallbackInfoReturnable<PackResourceMetadata> cir) {
		PackResourceMetadata metadata = cir.getReturnValue();
		int resourcePackFormat = JsonHelper.getInt(json, "fabric:resource_pack_format", metadata.getPackFormat());
		int dataPackFormat = JsonHelper.getInt(json, "fabric:data_pack_format", metadata.getPackFormat());
		((FabricPackResourceMetadata) metadata).setPackFormat(ResourceType.CLIENT_RESOURCES, resourcePackFormat);
		((FabricPackResourceMetadata) metadata).setPackFormat(ResourceType.SERVER_DATA, dataPackFormat);
	}
}
