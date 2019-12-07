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

/**
 * The packet receivers for the fabric networking API.
 *
 * <p>{@link net.fabricmc.fabric.api.networking.v1.receiver.PacketReceiver Packet receivers}
 * are channel-specific packet receivers, registered to packet receiver registries.
 *
 * <p>{@link net.fabricmc.fabric.api.networking.v1.receiver.PacketReceiverRegistry Packet receiver registries}
 * are where packet receivers are registered and unregistered from each channel. The
 * networking API also automatically sends packets in "minecraft:register" and
 * "minecraft:unregister" channels for receiver registration changes.
 *
 * <p>{@link net.fabricmc.fabric.api.networking.v1.receiver.ClientPacketReceiverRegistries}
 * offers access to packet receiver registries that exist on the logical client, while
 * {@link net.fabricmc.fabric.api.networking.v1.receiver.ServerPacketReceiverRegistries}
 * offers access to packet receiver registries that exist on the logical server.
 *
 * <p>{@link net.fabricmc.fabric.api.networking.v1.receiver.PacketContext Packet contexts}
 * are contexts offered to packet receivers so that they can retrieve the network handler,
 * execute tasks on the engine thread, etc.
 */

package net.fabricmc.fabric.api.networking.v1.receiver;
