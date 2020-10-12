package net.fabricmc.fabric.api.item.v1;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

@FunctionalInterface
public interface SoundPlayer {
    void playSound(World world,PlayerEntity player);
    
}
