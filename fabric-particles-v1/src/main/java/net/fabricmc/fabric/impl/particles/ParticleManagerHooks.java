package net.fabricmc.fabric.impl.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public interface ParticleManagerHooks {
	SpriteAtlasTexture fabric_getSpriteAtlasTexture();

	@Environment(EnvType.CLIENT)
	<T extends ParticleEffect> void fabric_registerCustomFactory(ParticleType<T> pt, ParticleFactory<T> pf);
}
