package net.fabricmc.fabric.mixin.network;

import net.fabricmc.fabric.impl.network.login.S2CLoginQueryQueueImpl;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginNetworkHandler.class)
public class MixinServerLoginNetworkHandler {
	@Unique
	private S2CLoginQueryQueueImpl loginQueue;
	@Shadow
	public ClientConnection client;

	@Inject(at = @At("HEAD"), method = "method_14384()V", cancellable = true)
	private void cancelAcceptIfAwaitingResponse(CallbackInfo info) {
		if (loginQueue == null) {
			//noinspection ConstantConditions
			ServerLoginNetworkHandler self = (ServerLoginNetworkHandler) (Object) this;
			loginQueue = new S2CLoginQueryQueueImpl(self);
		}

		if (loginQueue.tick()) {
			info.cancel();
		}
	}


	@Inject(at = @At("HEAD"), method = "onQueryResponse", cancellable = true)
	public void onQueryResponse(LoginQueryResponseC2SPacket packet, CallbackInfo info) {
		if (loginQueue.receiveResponse(packet)) {
			info.cancel();
		}
	}
}
