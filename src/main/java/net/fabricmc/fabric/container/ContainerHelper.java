package net.fabricmc.fabric.container;

import io.netty.buffer.Unpooled;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.packet.CustomPayloadClientPacket;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Helper/registry for handling custom containers. This class is used to register the container and send the packet to the client
 */
public class ContainerHelper {

	static final Identifier OPEN_CONTAINER = new Identifier("fabric", "open_container");
	private static final Map<Identifier, BiFunction<PlayerEntity, PacketByteBuf, Container>> containerMap = new HashMap<>();

	/**
	 * Use this in conjunction with the FabricContainerProvider aware openGui method
	 */
	public static final BiFunction<PlayerEntity, BlockPos, Container> CONTAINER_PROVIDER_FUNCTION = (playerEntity, pos) -> {
		BlockEntity blockEntity = playerEntity.world.getBlockEntity(pos);
		if (blockEntity instanceof FabricContainerProvider) {
			return ((FabricContainerProvider) blockEntity).createContainer(playerEntity.inventory, playerEntity);
		}
		return null;
	};

	/**
	 *
	 * @param identifier
	 * @param containerFunction
	 */
	public static void registerContainerHandler(Identifier identifier, BiFunction<PlayerEntity, PacketByteBuf, Container> containerFunction){
		containerMap.put(identifier, containerFunction);
	}

	public static void registerBlockContainerHandler(Identifier identifier, BiFunction<PlayerEntity, BlockPos, Container> containerFunction){
		registerContainerHandler(identifier, (playerEntity, packetByteBuf) -> {
			BlockPos pos = packetByteBuf.readBlockPos();
			return containerFunction.apply(playerEntity, pos);
		});
	}

	/**
	 *
	 * Sends a pack to the client to open the gui, and opens the container on the server side
	 *
	 * @param identifier the identifier that you registered your gui and container handler with
	 * @param byteBufConsumer a {@link PacketByteBuf} that you can write your own data to
	 * @param playerEntity the player that the gui should be opened on
	 */
	public static void openGui(Identifier identifier, Consumer<PacketByteBuf> byteBufConsumer, ServerPlayerEntity playerEntity) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeString(identifier.toString());
		byteBufConsumer.accept(buf);
		playerEntity.networkHandler.sendPacket(new CustomPayloadClientPacket(OPEN_CONTAINER, buf));

		playerEntity.container = containerMap.get(identifier).apply(playerEntity, buf);
		playerEntity.container.addListener(playerEntity);
	}

	/**
	 *  Opens a gui for a tile that implements FabricContainerProvider
	 * @param containerProvider the container provider
	 * @param pos The block pos of the container provider
	 * @param playerEntity The player that the gui will be opened on
	 */
	public static void openGui(FabricContainerProvider containerProvider, BlockPos pos, ServerPlayerEntity playerEntity) {
		openGui(containerProvider.getContainerIdentifier(), packetByteBuf -> packetByteBuf.writeBlockPos(pos), playerEntity);
	}
}
