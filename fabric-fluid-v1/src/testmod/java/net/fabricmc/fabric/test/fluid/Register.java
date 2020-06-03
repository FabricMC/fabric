package net.fabricmc.fabric.test.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.fluids.v1.properties.FluidProperty;
import net.fabricmc.fabric.api.fluids.v1.properties.FluidPropertyMerger;
import net.fabricmc.fabric.api.util.NbtIdentifier;
import net.fabricmc.fabric.api.util.NbtType;
import net.fabricmc.fabric.test.fluid.blocks.FluidBlockBlock;
import net.fabricmc.fabric.test.fluid.blocks.FluidBlockBlockEntity;
import net.fabricmc.fabric.test.fluid.items.FluidShard;

public class Register {
	private static final String ID = "fabric-fluid-v1-test";
	public static final FluidBlockBlock BLOCK = Registry.register(Registry.BLOCK, ID+":block", new FluidBlockBlock(Block.Settings.of(Material.STONE)));
	public static final BlockEntityType<FluidBlockBlockEntity> BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, ID+":block_entity", BlockEntityType.Builder.create(FluidBlockBlockEntity::new, BLOCK).build(null));
	public static final Item FLUID_SHARD = Registry.register(Registry.ITEM, ID+":fluid_shard", new FluidShard(new Item.Settings()));

	static void init() {
		FluidPropertyMerger.INSTANCE.register(new NbtIdentifier(ID, "temperature", NbtType.DOUBLE), new FluidProperty<DoubleTag>() {
			@Override
			public boolean areCompatible(Fluid fluid, DoubleTag aData, long aAmount, DoubleTag bData, long bAmount) {
				return true;
			}

			@Override
			public DoubleTag merge(Fluid fluid, DoubleTag aData, long aAmount, DoubleTag bData, long bAmount) {
				return DoubleTag.of((aData.getDouble() * aAmount + bData.getDouble() * bAmount) / 2);
			}
		});
	}
}
