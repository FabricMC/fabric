package net.fabricmc.fabric.mixin.resource.loader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.packet.Packet;
import net.minecraft.registry.VersionedIdentifier;
import net.minecraft.server.network.SynchronizeRegistriesTask;

@Mixin(SynchronizeRegistriesTask.class)
public abstract class SynchronizeRegistriesTaskMixin {

	@Shadow
	@Final
	private List<VersionedIdentifier> knownPacks;

	@Shadow
	protected abstract void syncRegistryAndTags(Consumer<Packet<?>> sender, Set<VersionedIdentifier> commonKnownPacks);

	@Inject(method = "onSelectKnownPacks", at= @At("HEAD"), cancellable = true)
	public void onSelectKnownPacks(List<VersionedIdentifier> clientKnownPacks, Consumer<Packet<?>> sender, CallbackInfo ci){
		if (new HashSet<>(this.knownPacks).containsAll(clientKnownPacks)) {
			this.syncRegistryAndTags(sender, Set.copyOf(clientKnownPacks));
			ci.cancel();
		}
	}

}
