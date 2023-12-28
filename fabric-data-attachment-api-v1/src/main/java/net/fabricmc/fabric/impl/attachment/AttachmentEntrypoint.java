package net.fabricmc.fabric.impl.attachment;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

public class AttachmentEntrypoint implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) ->
				AttachmentTargetImpl.copyOnRespawn((AttachmentTargetImpl) oldPlayer, (AttachmentTargetImpl) newPlayer, alive)
		);
	}
}
