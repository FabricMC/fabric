package net.fabricmc.fabric.impl.registry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FabricRegistryClientInit implements ClientModInitializer {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitializeClient() {
        ClientSidePacketRegistry.INSTANCE.register(RegistrySyncManager.ID, (ctx, buf) -> {
            // if not hosting server, apply packet
            RegistrySyncManager.receivePacket(ctx, buf, !MinecraftClient.getInstance().isInSingleplayer(), (e) -> {
                LOGGER.error("Registry remapping failed!", e);
                MinecraftClient.getInstance().execute(() -> {
                    ((ClientPlayerEntity) ctx.getPlayer()).networkHandler.getClientConnection().disconnect(
                            new StringTextComponent("Registry remapping failed: " + e.getMessage())
                    );
                });
            });
        });
    }
}
