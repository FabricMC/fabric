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

package net.fabricmc.fabric.impl.networking.payload;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.packet.CustomPayload;

import net.fabricmc.fabric.api.networking.v1.PacketType;

public sealed interface ResolvablePayload extends CustomPayload permits ResolvedPayload, RetainedPayload {
	/**
	 * Resolve the payload to one of the resolved types.
	 *
	 * @return {@link UntypedPayload} if type is {@code null}, {@link TypedPayload} if otherwise.
	 */
	ResolvedPayload resolve(@Nullable PacketType<?> type);

	/**
	 * @param type     the packet type, if it has any
	 * @param actual   the public handler that exposed to API consumer
	 * @param internal the internal handler
	 */
	record Handler<H>(@Nullable PacketType<?> type, Object actual, H internal) {
	}
}
