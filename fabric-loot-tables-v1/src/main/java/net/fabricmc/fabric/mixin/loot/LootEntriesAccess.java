package net.fabricmc.fabric.mixin.loot;

import net.minecraft.world.loot.entry.LootEntries;
import net.minecraft.world.loot.entry.LootEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LootEntries.class)
public interface LootEntriesAccess {
	@Invoker
	static void callRegister(LootEntry.Serializer<?> serializer) {
		throw new AssertionError("Mixin dummy");
	}
}
