package net.fabricmc.fabric.container;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.networking.CustomPayloadPacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * This class provides the client side helpers for ContainerHelper
 */
public class GuiHelper implements ClientModInitializer {

	private static final Map<Identifier, BiFunction<PlayerEntity, PacketByteBuf, Gui>> guiMap = new HashMap<>();

	/**
	 *
	 * Register a gui handler
	 *
	 * @param identifier the id for the gui, must be the same as the container
	 * @param containerFunction a function that you must return a gui
	 */
	public static void registerGuiHandler(Identifier identifier, BiFunction<PlayerEntity, PacketByteBuf, Gui> containerFunction){
		guiMap.put(identifier, containerFunction);
	}

	/**
	 *
	 * Same as registerGuiHandler just provides you with a block pos over a PacketByteBuf
	 *
	 * @param identifier the id for the gui, must be the same as the container
	 * @param guiFunction a function that you must return a gui
	 */
	public static void registerBlockGuiHandler(Identifier identifier, BiFunction<PlayerEntity, BlockPos, Gui> guiFunction){
		registerGuiHandler(identifier, (playerEntity, packetByteBuf) -> {
			BlockPos pos = packetByteBuf.readBlockPos();
			return guiFunction.apply(playerEntity, pos);
		});
	}

	@Override
	public void onInitializeClient() {
		CustomPayloadPacketRegistry.CLIENT.register(ContainerHelper.OPEN_CONTAINER, (packetContext, packetByteBuf) -> {
			Identifier identifier = new Identifier(packetByteBuf.readString(64));
			MinecraftClient.getInstance().execute(() -> {
				Gui gui = guiMap.get(identifier).apply(packetContext.getPlayer(), packetByteBuf);
				MinecraftClient.getInstance().openGui(gui);
			});
		});
	}
}
