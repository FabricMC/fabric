package net.fabricmc.fabric.test.mixin.resource.loader;


import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.packet.Packet;
import net.minecraft.registry.VersionedIdentifier;
import net.minecraft.server.network.SynchronizeRegistriesTask;

@Mixin(SynchronizeRegistriesTask.class)
public class SynchronizeRegistriesTaskMixin {
	@Unique private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizeRegistriesTaskMixin.class);

	@Inject(method = "syncRegistryAndTags", at=@At("HEAD"))
	public void syncRegistryAndTags(Consumer<Packet<?>> sender, Set<VersionedIdentifier> commonKnownPacks, CallbackInfo ci){
		if (commonKnownPacks.stream().noneMatch(knownPack -> knownPack.namespace().equals("fabric") && knownPack.id().equals("fabric-resource-loader-v0-testmod"))){
			LOGGER.error("fabric:fabric-resource-loader-v0-testmod is not in commonKnownPacks");
		}
		LOGGER.info("CommonKnownPacks: {}", commonKnownPacks);
	}
}
