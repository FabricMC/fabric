package net.fabricmc.fabric.impl.loot;

import java.util.Collection;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.function.LootFunction;

import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder;

/**
 * A compatible layer between the v1 and v2 loot table builders.
 */
@SuppressWarnings("deprecation")
public class DelegatingLootTableBuilder extends FabricLootSupplierBuilder {
	private final FabricLootTableBuilder delegate;

	public DelegatingLootTableBuilder(FabricLootTableBuilder delegate) {
		this.delegate = delegate;
	}

	@Override
	public FabricLootSupplierBuilder pool(LootPool.Builder pool) {
		delegate.pool(pool);
		return this;
	}

	@Override
	public FabricLootSupplierBuilder type(LootContextType type) {
		delegate.type(type);
		return this;
	}

	@Override
	public FabricLootSupplierBuilder apply(LootFunction.Builder function) {
		delegate.apply(function);
		return this;
	}

	@Override
	public FabricLootSupplierBuilder withPool(LootPool pool) {
		delegate.pool(pool);
		return this;
	}

	@Override
	public FabricLootSupplierBuilder withFunction(LootFunction function) {
		delegate.apply(function);
		return this;
	}

	@Override
	public FabricLootSupplierBuilder withPools(Collection<LootPool> pools) {
		delegate.pools(pools);
		return this;
	}

	@Override
	public FabricLootSupplierBuilder withFunctions(Collection<LootFunction> functions) {
		delegate.apply(functions);
		return this;
	}

	@Override
	public FabricLootSupplierBuilder copyFrom(LootTable supplier) {
		delegate.copyFrom(supplier);
		return this;
	}

	@Override
	public FabricLootSupplierBuilder copyFrom(LootTable supplier, boolean copyType) {
		delegate.copyFrom(supplier, copyType);
		return this;
	}

	@Override
	public FabricLootTableBuilder pool(LootPool pool) {
		delegate.pool(pool);
		return this;
	}

	@Override
	public FabricLootTableBuilder apply(LootFunction function) {
		delegate.apply(function);
		return this;
	}

	@Override
	public FabricLootTableBuilder pools(Collection<? extends LootPool> pools) {
		delegate.pools(pools);
		return this;
	}

	@Override
	public FabricLootTableBuilder apply(Collection<? extends LootFunction> functions) {
		delegate.apply(functions);
		return this;
	}
}
