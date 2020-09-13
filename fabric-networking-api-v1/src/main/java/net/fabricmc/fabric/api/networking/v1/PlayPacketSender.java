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

package net.fabricmc.fabric.api.networking.v1;

import net.minecraft.util.Identifier;

/**
 * Supports sending packets to channels in the play network handlers.
 *
 * <p>Compared to a simple packet sender, the play packet sender is informed if its connected recipient may {@link #hasChannel(Identifier) accept packets in certain channels}.
 * <!--TODO: Implement use of system property-->
 * When the {@code fabric-networking-api-v1.warnUnregisteredPackets} system property is absent or set to {@code true} and the recipient did not declare its ability to receive packets in a channel a packet was sent in, a warning is logged.</p>
 */
public interface PlayPacketSender extends PacketSender, ChannelRegistry {
}
