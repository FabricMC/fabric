package net.fabricmc.fabric.mixin.loot;

import com.google.gson.Gson;
import net.minecraft.world.loot.LootManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootManager.class)
public interface LootManagerAccess {
	@Accessor("GSON")
	static Gson getGson() {
		throw new AssertionError("Mixin dummy");
	}
}
