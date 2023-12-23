package net.fabricmc.fabric.test.attachment;

import com.mojang.serialization.Codec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.WorldChunk;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class PersistentAttachmentTest implements ModInitializer {
	public static final String MOD_ID = "fabric-data-attachment-api-v1-testmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private boolean firstLaunch = true;

	@Override
	public void onInitialize() {
		AttachmentType<String> dummy = AttachmentRegistry.createPersistent(
				new Identifier(MOD_ID, "persistent"),
				Codec.STRING
		);
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			ServerWorld overworld;
			WorldChunk chunk;

			if (firstLaunch) {
				LOGGER.info("First launch, setting up");

				overworld = server.getOverworld();
				overworld.setAttached(dummy, "world_data");

				chunk = overworld.getChunk(0, 0);
				chunk.setAttached(dummy, "chunk_data");
			} else {
				LOGGER.info("Second launch, testing");

				overworld = server.getOverworld();
				if (!"world_data".equals(overworld.getAttached(dummy))) throw new AssertionError();

				chunk = overworld.getChunk(0, 0);
				if (!"chunk_data".equals(chunk.getAttached(dummy))) throw new AssertionError();
			}
		});
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> firstLaunch = false);
	}
}
