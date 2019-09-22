package net.fabricmc.fabric.impl.datafixer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.datafixer.DataFixerUtils;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.event.server.ServerStopCallback;

public class FabricDataFixerInitalizerCommon implements ModInitializer {

    @Override
    public void onInitialize() {
        /**
         * 
         * Replace this as the Client Registers one injection for DataFixers so a client and common initalizer are needed.
         * 
         * There has to be a better way to lock registration of datafixers.
         * 
         */
        ServerStartCallback.EVENT.register((server) -> { // Run this when server starts so DataFixers can't be registered while server is running. This is to prevent world corruption from incompletely fixed chunks.
            if(!DataFixerUtils.INSTANCE.isLocked()) {
                FabricDataFixerImpl.INSTANCE.lock(true);
            }
        });
        
        ServerStopCallback.EVENT.register((server) -> { // Unlock on server shutdown so if a client starts another world, the datafixers will still initalize.
            FabricDataFixerImpl.INSTANCE.lock(false);
        });
    }

}
