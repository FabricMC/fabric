package net.fabricmc.fabric.impl.particles;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;

public interface VanillaParticleManager {

    SpriteAtlasTexture getAtlas();

    Int2ObjectMap<ParticleFactory<?>> getFactories();
}
