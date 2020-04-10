package net.fabricmc.fabric.mixin.client.texture;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Simply gets the {@link NativeImage[]} in Sprite.
 * Don't shy away from casting to this if you need to.
 */
@Mixin(Sprite.class)
public interface SpriteAccessor {
	@Accessor("images")
	abstract NativeImage[] getImages();
}
