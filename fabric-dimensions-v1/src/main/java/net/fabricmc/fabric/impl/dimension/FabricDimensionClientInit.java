package net.fabricmc.fabric.impl.dimension;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.impl.registry.RemapException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * Client entry point for fabric-dimensions
 */
public final class FabricDimensionClientInit {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void onClientInit() {
        ClientSidePacketRegistry.INSTANCE.register(DimensionIdsFixer.ID, (ctx, buf) -> {
			CompoundTag compound = buf.readCompoundTag();
			ctx.getTaskQueue().execute(() -> {
				if (compound == null) {
					handleError(ctx, new RemapException("Received null compound tag in dimension sync packet!"));
					return;
				}
				try {
					DimensionIdsFixer.apply(compound);
				} catch (RemapException e) {
					handleError(ctx, e);
				}
			});
		});
    }

	private static void handleError(PacketContext ctx, Exception e) {
		LOGGER.error("Dimension id remapping failed!", e);
		MinecraftClient.getInstance().execute(() -> ((ClientPlayerEntity) ctx.getPlayer()).networkHandler.getConnection().disconnect(
			new LiteralText("Dimension id remapping failed: " + e)
		));
	}

}
