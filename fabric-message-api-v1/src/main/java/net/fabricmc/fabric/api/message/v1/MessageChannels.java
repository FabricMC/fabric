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

package net.fabricmc.fabric.api.message.v1;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.util.Identifier;

/**
 * A class for controlling message channels. Channels provide a way for mods to control who sees which
 * messages in chat. This class provides methods to set and get the channel of a user or to check if
 * two users are in the same channel. The channels are all controlled on the server.
 *
 * <p>The channels are identifiers and don't need to be registered anywhere before they can be set to a
 * player. This easily allows for the dynamic creation of channels. It also means that channels don't
 * have to be removed when they are no longer being used.
 *
 * <p>This API is run completely on the server removing the need for anything on the client. This is
 * done to allow for mods like <a href="https://github.com/Patbox/StyledChat">StyledChat</a> or other similar server-side mods
 * to make use of this API.
 *
 * <p>An example of putting a player in a channel:
 *
 * <pre><code>
 *     MessageChannels.setChannel(player.getUuid(), new Identifier("example", "channel1"));
 * </code></pre>
 *
 * <p>An example of putting a player back into the global channel:
 *
 * <pre><code>
 *     MessageChannels.setChannel(player.getUuid(), MessageChannels.GLOBAL_CHANNEL);
 * </code></pre>
 */
public class MessageChannels {
	private MessageChannels() {
	}

	/**
	 * The default identifier all players start in.
	 */
	public static final Identifier GLOBAL_CHANNEL = new Identifier("fabric-message-api-v1", "global");

	private static final HashMap<UUID, Identifier> playerChannels = new HashMap<>();

	/**
	 * Set the channel of a player to an identifier. Used to move players into channels. It can be used to put players back into the global channel
	 *
	 * @param player The uuid of the player
	 * @param channel The channel the player should be put into
	 */
	public static void setChannel(UUID player, Identifier channel) {
		playerChannels.put(player, channel);
	}

	/**
	 * Get the channel a player is in. This could be used to perform styling to include the current channel on message.
	 *
	 * @param player The uuid of the player to check
	 * @return The identifier of the channel the player is inm returns the global channel by default
	 */
	public static Identifier getChannel(UUID player) {
		if (playerChannels.containsKey(player)) return playerChannels.get(player);

		return GLOBAL_CHANNEL;
	}

	/**
	 * Check if two players are in the same channel. Primarily used internally.
	 *
	 * @param player The uuid of the player receiving the message (interchangeable with sender)
	 * @param sender The uuid of the player sending the message
	 * @return Returns whether the two players are in the same channel
	 */
	public static boolean isInSameChannel(UUID player, UUID sender) {
		if (playerChannels.containsKey(player)) return playerChannels.get(player).equals(getChannel(sender));

		return getChannel(sender).equals(GLOBAL_CHANNEL);
	}
}
