package net.fabricmc.fabric.mixin.loot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.loot.entry.LootEntries;
import net.minecraft.loot.entry.LootEntry;

@Mixin(LootEntries.class)
public interface LootEntriesAccessor {
	@Invoker
	static void callRegister(LootEntry.Serializer<?> serializer) {
		throw new AssertionError("@Invoker body called!");
	}
}
