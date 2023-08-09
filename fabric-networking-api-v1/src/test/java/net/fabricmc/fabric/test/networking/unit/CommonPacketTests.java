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

package net.fabricmc.fabric.test.networking.unit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import net.minecraft.client.network.ClientConfigurationNetworkHandler;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.impl.networking.ChannelInfoHolder;
import net.fabricmc.fabric.impl.networking.CommonPacketHandler;
import net.fabricmc.fabric.impl.networking.CommonPacketsImpl;
import net.fabricmc.fabric.impl.networking.CommonRegisterPayload;
import net.fabricmc.fabric.impl.networking.CommonVersionPayload;
import net.fabricmc.fabric.impl.networking.client.ClientConfigurationNetworkAddon;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;
import net.fabricmc.fabric.impl.networking.server.ServerConfigurationNetworkAddon;
import net.fabricmc.fabric.impl.networking.server.ServerNetworkingImpl;

public class CommonPacketTests {
	private PacketSender packetSender;
	private ChannelInfoHolder channelInfoHolder;

	private ClientConfigurationNetworkHandler clientNetworkHandler;
	private ClientConfigurationNetworkAddon clientAddon;

	private ServerConfigurationNetworkHandler serverNetworkHandler;
	private ServerConfigurationNetworkAddon serverAddon;

	@BeforeAll
	static void beforeAll() {
		CommonPacketsImpl.init();
		ClientNetworkingImpl.clientInit();

		// Register a receiver to send in the play registry response
		ClientPlayNetworking.registerGlobalReceiver(new Identifier("fabric", "global_client"), (client, handler, buf, responseSender) -> {
		});
	}

	@BeforeEach
	void setUp() {
		packetSender = mock(PacketSender.class);
		channelInfoHolder = new MockChannelInfoHolder();

		clientNetworkHandler = mock(ClientConfigurationNetworkHandler.class);
		clientAddon = mock(ClientConfigurationNetworkAddon.class);
		when(ClientNetworkingImpl.getAddon(clientNetworkHandler)).thenReturn(clientAddon);
		when(clientAddon.getChannelInfoHolder()).thenReturn(channelInfoHolder);

		serverNetworkHandler = mock(ServerConfigurationNetworkHandler.class);
		serverAddon = mock(ServerConfigurationNetworkAddon.class);
		when(ServerNetworkingImpl.getAddon(serverNetworkHandler)).thenReturn(serverAddon);
		when(serverAddon.getChannelInfoHolder()).thenReturn(channelInfoHolder);
	}

	// Test handling the version packet on the client
	@Test
	void handleVersionPacketClient() {
		ClientConfigurationNetworking.ConfigurationChannelHandler packetHandler = ClientNetworkingImpl.CONFIGURATION.getHandler(CommonVersionPayload.PACKET_ID);
		assertNotNull(packetHandler);

		// Receive a packet from the server
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeIntArray(new int[]{1, 2, 3});

		packetHandler.receive(null, clientNetworkHandler, buf, packetSender);

		// Assert the entire packet was read
		assertEquals(0, buf.readableBytes());

		// Check the response we are sending back to the server
		PacketByteBuf response = readResponse(packetSender);
		assertArrayEquals(new int[]{1}, response.readIntArray());
		assertEquals(0, response.readableBytes());

		assertEquals(1, getNegotiatedVersion(clientAddon));
	}

	// Test handling the version packet on the client, when the server sends unsupported versions
	@Test
	void handleVersionPacketClientUnsupported() {
		ClientConfigurationNetworking.ConfigurationChannelHandler packetHandler = ClientNetworkingImpl.CONFIGURATION.getHandler(CommonVersionPayload.PACKET_ID);
		assertNotNull(packetHandler);

		// Receive a packet from the server
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeIntArray(new int[]{2, 3}); // We only support version 1

		assertThrows(UnsupportedOperationException.class, () -> {
			packetHandler.receive(null, clientNetworkHandler, buf, packetSender);
		});

		// Assert the entire packet was read
		assertEquals(0, buf.readableBytes());
	}

	// Test handling the version packet on the server
	@Test
	void handleVersionPacketServer() {
		ServerConfigurationNetworking.ConfigurationChannelHandler packetHandler = ServerNetworkingImpl.CONFIGURATION.getHandler(CommonVersionPayload.PACKET_ID);
		assertNotNull(packetHandler);

		// Receive a packet from the client
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeIntArray(new int[]{1, 2, 3});

		packetHandler.receive(null, serverNetworkHandler, buf, null);

		// Assert the entire packet was read
		assertEquals(0, buf.readableBytes());
		assertEquals(1, getNegotiatedVersion(serverAddon));
	}

	// Test handling the version packet on the server unsupported version
	@Test
	void handleVersionPacketServerUnsupported() {
		ServerConfigurationNetworking.ConfigurationChannelHandler packetHandler = ServerNetworkingImpl.CONFIGURATION.getHandler(CommonVersionPayload.PACKET_ID);
		assertNotNull(packetHandler);

		// Receive a packet from the client
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeIntArray(new int[]{3}); // Server only supports version 1

		assertThrows(UnsupportedOperationException.class, () -> {
			packetHandler.receive(null, serverNetworkHandler, buf, packetSender);
		});

		// Assert the entire packet was read
		assertEquals(0, buf.readableBytes());
	}

	// Test handing the play registry packet on the client configuration handler
	@Test
	void handlePlayRegistryClient() {
		ClientConfigurationNetworking.ConfigurationChannelHandler packetHandler = ClientNetworkingImpl.CONFIGURATION.getHandler(CommonRegisterPayload.PACKET_ID);
		assertNotNull(packetHandler);

		when(clientAddon.getNegotiatedVersion()).thenReturn(1);

		// Receive a packet from the server
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeVarInt(1); // Version
		buf.writeString("play"); // Target phase
		buf.writeCollection(List.of(new Identifier("fabric", "test")), PacketByteBuf::writeIdentifier);

		packetHandler.receive(null, clientNetworkHandler, buf, packetSender);

		// Assert the entire packet was read
		assertEquals(0, buf.readableBytes());
		assertIterableEquals(List.of(new Identifier("fabric", "test")), channelInfoHolder.getPendingChannelsNames(NetworkState.PLAY));

		// Check the response we are sending back to the server
		PacketByteBuf response = readResponse(packetSender);
		assertEquals(1, response.readVarInt());
		assertEquals("play", response.readString());
		assertIterableEquals(List.of(new Identifier("fabric", "global_client")), response.readCollection(HashSet::new, PacketByteBuf::readIdentifier));
		assertEquals(0, response.readableBytes());
	}

	// Test handling the configuration registry packet on the client configuration handler
	@Test
	void handleConfigurationRegistryClient() {
		ClientConfigurationNetworking.ConfigurationChannelHandler packetHandler = ClientNetworkingImpl.CONFIGURATION.getHandler(CommonRegisterPayload.PACKET_ID);
		assertNotNull(packetHandler);

		when(clientAddon.getNegotiatedVersion()).thenReturn(1);
		when(clientAddon.createRegisterPayload()).thenAnswer(i -> new CommonRegisterPayload(1, "configuration", Set.of(new Identifier("fabric", "global_configuration_client"))));

		// Receive a packet from the server
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeVarInt(1); // Version
		buf.writeString("configuration"); // Target phase
		buf.writeCollection(List.of(new Identifier("fabric", "test")), PacketByteBuf::writeIdentifier);

		packetHandler.receive(null, clientNetworkHandler, buf, packetSender);

		// Assert the entire packet was read
		assertEquals(0, buf.readableBytes());
		verify(clientAddon, times(1)).onCommonRegisterPacket(any());

		// Check the response we are sending back to the server
		PacketByteBuf response = readResponse(packetSender);
		assertEquals(1, response.readVarInt());
		assertEquals("configuration", response.readString());
		assertIterableEquals(List.of(new Identifier("fabric", "global_configuration_client")), response.readCollection(HashSet::new, PacketByteBuf::readIdentifier));
		assertEquals(0, response.readableBytes());
	}

	// Test handing the play registry packet on the server configuration handler
	@Test
	void handlePlayRegistryServer() {
		ServerConfigurationNetworking.ConfigurationChannelHandler packetHandler = ServerNetworkingImpl.CONFIGURATION.getHandler(CommonRegisterPayload.PACKET_ID);
		assertNotNull(packetHandler);

		when(serverAddon.getNegotiatedVersion()).thenReturn(1);

		// Receive a packet from the client
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeVarInt(1); // Version
		buf.writeString("play"); // Target phase
		buf.writeCollection(List.of(new Identifier("fabric", "test")), PacketByteBuf::writeIdentifier);

		packetHandler.receive(null, serverNetworkHandler, buf, packetSender);

		// Assert the entire packet was read
		assertEquals(0, buf.readableBytes());
		assertIterableEquals(List.of(new Identifier("fabric", "test")), channelInfoHolder.getPendingChannelsNames(NetworkState.PLAY));
	}

	// Test handing the configuration registry packet on the server configuration handler
	@Test
	void handleConfigurationRegistryServer() {
		ServerConfigurationNetworking.ConfigurationChannelHandler packetHandler = ServerNetworkingImpl.CONFIGURATION.getHandler(CommonRegisterPayload.PACKET_ID);
		assertNotNull(packetHandler);

		when(serverAddon.getNegotiatedVersion()).thenReturn(1);

		// Receive a packet from the client
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeVarInt(1); // Version
		buf.writeString("configuration"); // Target phase
		buf.writeCollection(List.of(new Identifier("fabric", "test")), PacketByteBuf::writeIdentifier);

		packetHandler.receive(null, serverNetworkHandler, buf, packetSender);

		// Assert the entire packet was read
		assertEquals(0, buf.readableBytes());
		verify(serverAddon, times(1)).onCommonRegisterPacket(any());
	}

	@Test
	public void testHighestCommonVersionWithCommonElement() {
		int[] a = {1, 2, 3};
		int[] b = {1, 2};
		assertEquals(2, CommonPacketsImpl.getHighestCommonVersion(a, b));
	}

	@Test
	public void testHighestCommonVersionWithoutCommonElement() {
		int[] a = {1, 3, 5};
		int[] b = {2, 4, 6};
		assertEquals(-1, CommonPacketsImpl.getHighestCommonVersion(a, b));
	}

	@Test
	public void testHighestCommonVersionWithOneEmptyArray() {
		int[] a = {1, 3, 5};
		int[] b = {};
		assertEquals(-1, CommonPacketsImpl.getHighestCommonVersion(a, b));
	}

	@Test
	public void testHighestCommonVersionWithBothEmptyArrays() {
		int[] a = {};
		int[] b = {};
		assertEquals(-1, CommonPacketsImpl.getHighestCommonVersion(a, b));
	}

	@Test
	public void testHighestCommonVersionWithIdenticalArrays() {
		int[] a = {1, 2, 3};
		int[] b = {1, 2, 3};
		assertEquals(3, CommonPacketsImpl.getHighestCommonVersion(a, b));
	}

	private static PacketByteBuf readResponse(PacketSender packetSender) {
		ArgumentCaptor<CustomPayload> responseCaptor = ArgumentCaptor.forClass(CustomPayload.class);
		verify(packetSender, times(1)).sendPacket(responseCaptor.capture());

		PacketByteBuf buf = PacketByteBufs.create();
		responseCaptor.getValue().write(buf);

		return buf;
	}

	private static int getNegotiatedVersion(CommonPacketHandler packetHandler) {
		ArgumentCaptor<Integer> responseCaptor = ArgumentCaptor.forClass(Integer.class);
		verify(packetHandler, times(1)).onCommonVersionPacket(responseCaptor.capture());
		return responseCaptor.getValue();
	}

	private static class MockChannelInfoHolder implements ChannelInfoHolder {
		private final Map<NetworkState, Collection<Identifier>> playChannels = new ConcurrentHashMap<>();

		@Override
		public Collection<Identifier> getPendingChannelsNames(NetworkState state) {
			return this.playChannels.computeIfAbsent(state, (key) -> Collections.newSetFromMap(new ConcurrentHashMap<>()));
		}
	}
}
