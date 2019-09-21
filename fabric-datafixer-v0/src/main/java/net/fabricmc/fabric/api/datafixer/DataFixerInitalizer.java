package net.fabricmc.fabric.api.datafixer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.event.server.ServerStopCallback;

public class DataFixerInitalizer implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerStartCallback.EVENT.register((server) -> { // Run this when server starts so DataFixers can't be registered while server is running. This is to prevent world corruption from incompletely fixed chunks.
            if(!FabricDataFixerUtils.LOCKED) {
                FabricDataFixerUtils.LOCKED = true;
            }
        });
        
        ServerStopCallback.EVENT.register((server) -> { // Unlock on server shutdown so if a client starts another world, the datafixers will still initalize.
            FabricDataFixerUtils.LOCKED = false;
        });
    }

}
