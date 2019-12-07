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

package net.fabricmc.fabric.mixin.networking.handshake;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.server.ServerMetadata;

import net.fabricmc.fabric.impl.networking.handshake.FabricExtraMeta;
import net.fabricmc.fabric.impl.networking.handshake.FabricExtraMetaHolder;

@Mixin(ServerMetadata.class)
public abstract class ServerMetadataMixin implements FabricExtraMetaHolder {
	private FabricExtraMeta extraMeta;

	@Override
	public FabricExtraMeta getFabricExtraMeta() {
		return extraMeta;
	}

	@Override
	public void setFabricExtraMeta(FabricExtraMeta meta) {
		this.extraMeta = meta;
	}
}
