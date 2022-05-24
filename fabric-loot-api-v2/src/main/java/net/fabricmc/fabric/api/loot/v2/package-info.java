/**
 * The Fabric Loot API for manipulating and creating loot tables.
 *
 * <h2>Events</h2>
 * {@link net.fabricmc.fabric.api.loot.v2.LootTableEvents} has events to modify existing loot tables,
 * or outright replace them with a new loot table.
 *
 * <p>You can also check where loot tables are coming from in those events with
 * {@link net.fabricmc.fabric.api.loot.v2.LootTableSource}. This is useful when you only want to modify
 * loot tables from mods or vanilla, but not user-created data packs.
 *
 * <h2>Extended loot table and pool builders</h2>
 * This API has injected interfaces to add useful methods to
 * {@linkplain net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder loot table} and
 * {@linkplain net.fabricmc.fabric.api.loot.v2.FabricLootPoolBuilder loot pool} builders.
 * They let you add pre-built objects instead of builders, and collections of objects to the builder
 * with one method call.
 */
package net.fabricmc.fabric.api.loot.v2;
