package net.fabricmc.fabric.mixin.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.particles.ParticleRegistry;
import net.fabricmc.fabric.impl.particles.ParticleManagerHooks;
import net.fabricmc.fabric.impl.particles.ParticleRegistryImpl;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(ParticleManager.class)
abstract class MixinParticleManager implements ParticleManagerHooks {
	@Shadow
	private SpriteAtlasTexture particleAtlasTexture;
	public SpriteAtlasTexture fabric_getSpriteAtlasTexture() { return particleAtlasTexture; }

	@Shadow
	@Environment(EnvType.CLIENT)
	private <T extends ParticleEffect> void registerFactory(ParticleType<T> pt, ParticleFactory<T> pf) {}
	@Environment(EnvType.CLIENT)
	public <T extends ParticleEffect> void fabric_registerCustomFactory(ParticleType<T> pt, ParticleFactory<T> pf) { registerFactory(pt,pf); }

	@Inject(method = "net/minecraft/client/particle/ParticleManager.registerDefaultFactories()V", at = @At("RETURN"))
	private void registerCustomFactories(CallbackInfo cbi) {
		HashMap<ParticleType<?>, ParticleFactory<?>> factories = ((ParticleRegistryImpl)ParticleRegistry.INSTANCE).factoriesAwaitingRegistry;
		if(!factories.isEmpty()) {
			factories.forEach((type, factory) -> this.registerFactory((ParticleType)type, factory));
			factories.clear();
		}
	}
}
