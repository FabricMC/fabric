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

package net.fabricmc.fabric.impl.dimension;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.impl.registry.sync.RemapException;

/**
 * Client entry point for fabric-dimensions.
 */
public final class FabricDimensionClientInit {
	private static final Logger LOGGER = LogManager.getLogger();

	public static void onClientInit() {
		ClientSidePacketRegistry.INSTANCE.register(DimensionIdsFixer.ID, (ctx, buf) -> {
			CompoundTag compound = buf.readCompoundTag();

			ctx.getTaskQueue().execute(() -> {
				if (compound == null) {
					handleError(ctx, new RemapException("Received null compound tag in dimension sync packet!"));
					return;
				}

				try {
					DimensionIdsFixer.apply(compound);
				} catch (RemapException e) {
					handleError(ctx, e);
				}
			});
		});
	}

	private static void handleError(PacketContext ctx, Exception e) {
		LOGGER.error("Dimension id remapping failed!", e);

		MinecraftClient.getInstance().execute(() -> ((ClientPlayerEntity) ctx.getPlayer()).networkHandler.getConnection().disconnect(
				new LiteralText("Dimension id remapping failed: " + e)
		));
	}
}
