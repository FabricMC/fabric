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

package net.fabricmc.fabric.impl.networking;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import io.netty.util.AsciiString;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;

/**
 * A network addon which is aware of the channels the other side may receive.
 *
 * @param <H> the channel handler type
 */
public abstract class AbstractChanneledNetworkAddon<H> extends AbstractNetworkAddon<H> implements PacketSender, CommonPacketHandler {
	protected final ClientConnection connection;
	protected final GlobalReceiverRegistry<H> receiver;
	protected final Set<Identifier> sendableChannels;

	protected int commonVersion = -1;

	protected AbstractChanneledNetworkAddon(GlobalReceiverRegistry<H> receiver, ClientConnection connection, String description) {
		super(receiver, description);
		this.connection = connection;
		this.receiver = receiver;
		this.sendableChannels = Collections.synchronizedSet(new HashSet<>());
	}

	public abstract void lateInit();

	protected void registerPendingChannels(ChannelInfoHolder holder, NetworkState state) {
		final Collection<Identifier> pending = holder.getPendingChannelsNames(state);

		if (!pending.isEmpty()) {
			register(new ArrayList<>(pending));
			pending.clear();
		}
	}

	// always supposed to handle async!
	protected boolean handle(Identifier channelName, PacketByteBuf buf) {
		this.logger.debug("Handling inbound packet from channel with name \"{}\"", channelName);

		// Handle reserved packets
		if (NetworkingImpl.REGISTER_CHANNEL.equals(channelName)) {
			this.receiveRegistration(true, buf);
			return true;
		}

		if (NetworkingImpl.UNREGISTER_CHANNEL.equals(channelName)) {
			this.receiveRegistration(false, buf);
			return true;
		}

		@Nullable H handler = this.getHandler(channelName);

		if (handler == null) {
			return false;
		}

		try {
			this.receive(handler, buf);
		} catch (Throwable ex) {
			this.logger.error("Encountered exception while handling in channel with name \"{}\"", channelName, ex);
			throw ex;
		}

		return true;
	}

	protected abstract void receive(H handler, PacketByteBuf buf);

	protected void sendInitialChannelRegistrationPacket() {
		final PacketByteBuf buf = this.createRegistrationPacket(this.getReceivableChannels());

		if (buf != null) {
			this.sendPacket(NetworkingImpl.REGISTER_CHANNEL, buf);
		}
	}

	@Nullable
	protected PacketByteBuf createRegistrationPacket(Collection<Identifier> channels) {
		if (channels.isEmpty()) {
			return null;
		}

		PacketByteBuf buf = PacketByteBufs.create();
		boolean first = true;

		for (Identifier channel : channels) {
			if (first) {
				first = false;
			} else {
				buf.writeByte(0);
			}

			buf.writeBytes(channel.toString().getBytes(StandardCharsets.US_ASCII));
		}

		return buf;
	}

	// wrap in try with res (buf)
	protected void receiveRegistration(boolean register, PacketByteBuf buf) {
		List<Identifier> ids = new ArrayList<>();
		StringBuilder active = new StringBuilder();

		while (buf.isReadable()) {
			byte b = buf.readByte();

			if (b != 0) {
				active.append(AsciiString.b2c(b));
			} else {
				this.addId(ids, active);
				active = new StringBuilder();
			}
		}

		this.addId(ids, active);

		if (register) {
			register(ids);
		} else {
			unregister(ids);
		}
	}

	void register(List<Identifier> ids) {
		this.sendableChannels.addAll(ids);
		schedule(() -> this.invokeRegisterEvent(ids));
	}

	void unregister(List<Identifier> ids) {
		this.sendableChannels.removeAll(ids);
		schedule(() -> this.invokeUnregisterEvent(ids));
	}

	@Override
	public void sendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
		sendPacket(packet, GenericFutureListenerHolder.create(callback));
	}

	@Override
	public void sendPacket(Packet<?> packet, PacketCallbacks callback) {
		Objects.requireNonNull(packet, "Packet cannot be null");

		this.connection.send(packet, callback);
	}

	/**
	 * Schedules a task to run on the main thread.
	 */
	protected abstract void schedule(Runnable task);

	protected abstract void invokeRegisterEvent(List<Identifier> ids);

	protected abstract void invokeUnregisterEvent(List<Identifier> ids);

	private void addId(List<Identifier> ids, StringBuilder sb) {
		String literal = sb.toString();

		try {
			ids.add(new Identifier(literal));
		} catch (InvalidIdentifierException ex) {
			this.logger.warn("Received invalid channel identifier \"{}\" from connection {}", literal, this.connection);
		}
	}

	public Set<Identifier> getSendableChannels() {
		return Collections.unmodifiableSet(this.sendableChannels);
	}

	// Common packet handlers

	@Override
	public void onCommonVersionPacket(int negotiatedVersion) {
		assert negotiatedVersion == 1; // We only support version 1 for now

		commonVersion = negotiatedVersion;
		this.logger.info("Negotiated common packet version {}", commonVersion);
	}

	@Override
	public void onCommonRegisterPacket(CommonRegisterPayload payload) {
		if (payload.version() != getNegotiatedVersion()) {
			throw new IllegalStateException("Negotiated common packet version: %d but received packet with version: %d".formatted(commonVersion, payload.version()));
		}

		final String currentPhase = getPhase();

		if (currentPhase == null) {
			// We don't support receiving the register packet during this phase. See getPhase() for supported phases.
			// The normal case where the play channels are sent during configuration is handled in the client/common configuration packet handlers.
			logger.warn("Received common register packet for phase {} in network state: {}", payload.phase(), receiver.getState());
			return;
		}

		if (!payload.phase().equals(currentPhase)) {
			// We need to handle receiving the play phase during configuration!
			throw new IllegalStateException("Register packet received for phase (%s) on handler for phase(%s)".formatted(payload.phase(), currentPhase));
		}

		register(new ArrayList<>(payload.channels()));
	}

	@Override
	public CommonRegisterPayload createRegisterPayload() {
		return new CommonRegisterPayload(getNegotiatedVersion(), getPhase(), this.getReceivableChannels());
	}

	@Override
	public int getNegotiatedVersion() {
		if (commonVersion == -1) {
			throw new IllegalStateException("Not yet negotiated common packet version");
		}

		return commonVersion;
	}

	@Nullable
	private String getPhase() {
		return switch (receiver.getState()) {
		case PLAY -> CommonRegisterPayload.PLAY_PHASE;
		case CONFIGURATION -> CommonRegisterPayload.CONFIGURATION_PHASE;
		default -> null; // We don't support receiving this packet on any other phase
		};
	}
}
