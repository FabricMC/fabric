package net.fabricmc.fabric.api.particles;

import net.fabricmc.fabric.impl.particles.ParticleManagerHooks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public abstract class FabricSpriteParticle extends SpriteBillboardParticle {
	public FabricSpriteParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		super(world, x, y, z, vx, vy, vz);

		SpriteAtlasTexture sat = ((ParticleManagerHooks)MinecraftClient.getInstance().particleManager).fabric_getSpriteAtlasTexture();
		this.setSprite(sat.getSprite(this.getSprite()));
	}

	protected abstract Identifier getSprite();
	public ParticleTextureSheet getType() { return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE; }
}
