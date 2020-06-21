package net.fabricmc.fabric.mixin.client.particle;

import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

@Mixin(ParticleManager.class)
public interface ParticleManagerAccessor {
	@Accessor("particleAtlasTexture")
	SpriteAtlasTexture getParticleAtlasTexture();

	@Accessor("factories")
	Int2ObjectMap<ParticleFactory<?>> getFactories();

	// NOTE: The field signature is actually Map<Identifier, SimpleSpriteProvider>
	// This still works due to type erasure
	@Accessor("field_18300")
	Map<Identifier, SpriteProvider> getSpriteAwareFactories();

	@Mixin(targets = "net/minecraft/client/particle/ParticleManager$SimpleSpriteProvider")
	interface SimpleSpriteProviderAccessor {
		@Accessor("sprites")
		List<Sprite> getSprites();
	}
}
