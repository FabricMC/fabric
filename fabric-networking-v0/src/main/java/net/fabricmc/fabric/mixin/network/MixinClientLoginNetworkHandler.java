package net.fabricmc.fabric.mixin.network;

import net.fabricmc.fabric.impl.network.login.ClientLoginQueryResponder;
import net.fabricmc.fabric.impl.network.login.ClientLoginQueryResponseRegistry;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.packet.LoginQueryRequestS2CPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;
import net.minecraft.util.Language;
import net.minecraft.util.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(ClientLoginNetworkHandler.class)
public class MixinClientLoginNetworkHandler {
	@Shadow
	private Consumer<Component> statusConsumer;
	@Shadow
	private ClientConnection connection;

	@Inject(at = @At("HEAD"), method = "onQueryRequest", cancellable = true)
	public void onQueryRequest(LoginQueryRequestS2CPacket packet, CallbackInfo info) {
		//noinspection ConstantConditions
		ClientLoginNetworkHandler self = (ClientLoginNetworkHandler) (Object) this;
		Optional<LoginQueryResponseC2SPacket> responseOptional = ClientLoginQueryResponseRegistry.INSTANCE.respond(self, connection, packet);
		responseOptional.ifPresent((response) -> {
			this.statusConsumer.accept(new TranslatableComponent("connect.negotiating"));
			this.connection.send(response);
			info.cancel();
		});
	}
}
