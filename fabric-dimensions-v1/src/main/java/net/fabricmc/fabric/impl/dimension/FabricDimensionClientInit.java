package net.fabricmc.fabric.impl.dimension;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.LiteralText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Client entry point for fabric-dimensions
 */
public final class FabricDimensionClientInit {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void onClientInit() {
        ClientSidePacketRegistry.INSTANCE.register(DimensionIdsFixer.ID, (ctx, buf) -> DimensionIdsFixer.receivePacket(ctx, buf, (e) -> {
            LOGGER.error("Dimension id remapping failed!", e);
            MinecraftClient.getInstance().execute(() -> ((ClientPlayerEntity) ctx.getPlayer()).networkHandler.getConnection().disconnect(
                    new LiteralText("Dimension id remapping failed: " + e.getMessage())
            ));
        }));
    }

}
