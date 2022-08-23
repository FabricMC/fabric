package net.fabricmc.fabric.api.message.v1;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.UUID;

public class MessageChannels {
	public static final Identifier GLOBAL_CHANNEL = new Identifier("fabric-message-api-v1", "global");

	private static final HashMap<UUID, Identifier> STORE = new HashMap<>();

	private MessageChannels() {
	}

	public static void setUser(UUID user, Identifier channel) {
		STORE.put(user, channel);
	}

	public static Identifier getChannel(UUID user) {
		if (STORE.containsKey(user)) return STORE.get(user);

		return GLOBAL_CHANNEL;
	}

	public static boolean isInSameChannel(UUID user, UUID sender) {
		if (STORE.containsKey(user)) return STORE.get(user).equals(getChannel(sender));

		return getChannel(sender).equals(GLOBAL_CHANNEL);
	}
}
