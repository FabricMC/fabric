package net.fabricmc.fabric.impl.network;

import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.PacketRegistry;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class PacketRegistryImpl implements PacketRegistry {
	private static final Identifier MC_REGISTER = new Identifier("minecraft:register");
	private static final Identifier MC_UNREGISTER = new Identifier("minecraft:unregister");

	protected final Map<Identifier, PacketConsumer> consumerMap;

	PacketRegistryImpl() {
		consumerMap = new LinkedHashMap<>();
	}

	public Collection<Identifier> getRegisteredPacketIds() {
		return consumerMap.keySet();
	}

	@Override
	public void register(Identifier id, PacketConsumer consumer) {
		boolean isNew = true;
		if (consumerMap.containsKey(id)) {
			// TODO: log warning
			isNew = false;
		}

		consumerMap.put(id, consumer);
		if (isNew) {
			onRegister(id);
		}
	}

	@Override
	public void unregister(Identifier id) {
		consumerMap.remove(id);
		onUnregister(id);
	}

	protected abstract void onRegister(Identifier id);
	protected abstract void onUnregister(Identifier id);
	protected abstract Collection<Identifier> getIdCollectionFor(PacketContext context);

	/**
	 * Hook for accepting packets used in Fabric mixins.
	 *
	 * @param id The packet Identifier received.
	 * @param context The packet context provided.
	 * @param buf The packet data buffer received.
	 * @return Whether or not the packet was handled by this packet registry.
	 */
	public boolean accept(Identifier id, PacketContext context, PacketByteBuf buf) {
		if (id.equals(MC_REGISTER) || id.equals(MC_UNREGISTER)) {
			Collection<Identifier> ids = new HashSet<>();
			while (buf.readerIndex() < buf.writerIndex() /* TODO: check correctness */) {
				Identifier newId = new Identifier(buf.readString(32767));
				ids.add(newId);
			}

			Collection<Identifier> target = getIdCollectionFor(context);
			if (id.equals(MC_UNREGISTER)) {
				target.removeAll(ids);
			} else {
				target.addAll(ids);
			}
			return false; // continue execution for other mods
		}

		PacketConsumer consumer = consumerMap.get(id);
		if (consumer != null) {
			try {
				consumer.accept(context, buf);
			} catch (Throwable t) {
				// TODO: handle better
				t.printStackTrace();
			}
			return true;
		} else {
			return false;
		}
	}
}
