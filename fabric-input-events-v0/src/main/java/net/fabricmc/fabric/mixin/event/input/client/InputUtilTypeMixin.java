package net.fabricmc.fabric.mixin.event.input.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

@Mixin(InputUtil.Type.class)
public interface InputUtilTypeMixin {
	@Accessor("map")
	public Int2ObjectMap<Key> fabric_getMap();
}
