package net.fabricmc.fabric.mixin.item.shears;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.entry.RegistryEntry;

import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.impl.item.ShearsHelper;
import net.fabricmc.fabric.mixin.item.shears.accessors.DirectRegistryEntryListAccessor;

@Mixin(MatchToolLootCondition.class)
public abstract class MatchToolLootConditionMixin implements LootCondition {
	@Unique
	private static final List<ItemPredicate> MATCH_TOOL_PREDICATES = new ArrayList<>();

	@Inject(at = @At("RETURN"), method = "<init>")
	private void shearsLoot(CallbackInfo ci) {
		// allows anything in fabric:shears to mine grass (and other stuff) and it will drop
		((MatchToolLootCondition)(Object)this).predicate().ifPresent(MATCH_TOOL_PREDICATES::add);
	}

	static {
		// loot is loaded before tags, so this is required
		CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {
			if (!client) {
				for (ItemPredicate p : MATCH_TOOL_PREDICATES) {
					//noinspection deprecation
					if (p.items().isPresent() && p.items().get().contains(Items.SHEARS.getRegistryEntry())) {
						@SuppressWarnings("unchecked")
						DirectRegistryEntryListAccessor<Item> accessor = ((DirectRegistryEntryListAccessor<Item>)p.items().get());
						ImmutableList.Builder<RegistryEntry<Item>> builder = new ImmutableList.Builder<>();
						builder.addAll(accessor.getEntries());
						builder.addAll(ShearsHelper.SHEARS);
						accessor.setEntries(builder.build());
						accessor.setEntrySet(null);
					}
				}
			}
			MATCH_TOOL_PREDICATES.clear();
		});
	}
}
