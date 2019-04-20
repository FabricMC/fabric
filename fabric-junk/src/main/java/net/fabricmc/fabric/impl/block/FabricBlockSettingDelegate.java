package net.fabricmc.fabric.impl.block;

import net.minecraft.block.MaterialColor;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public interface FabricBlockSettingDelegate {
    void fabric_setMaterialColor(MaterialColor color);
    void fabric_setCollidable(boolean value);
    void fabric_setSoundGroup(BlockSoundGroup group);
    void fabric_setLightLevel(int value);
    void fabric_setHardness(float value);
    void fabric_setResistance(float value);
    void fabric_setRandomTicks(boolean value);
    void fabric_setFriction(float value);
    void fabric_setDropTable(Identifier id);
    void fabric_setDynamicBounds(boolean value);
}
