package net.fabricmc.fabric.test.transaction;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.fabricmc.fabric.api.transaction.v1.FullCopyDataKey;
import net.fabricmc.fabric.api.transaction.v1.Transaction;

import java.util.Objects;

public class CauldronTransactionSupport {
	private final DataKey key;

	private CauldronTransactionSupport(World world, BlockPos pos, BlockState state) {
		this.key = new DataKey(world, pos, state);
	}

	public int insert(Transaction ta, int amount) {
		BlockState currentState = this.key.getCurrentState(ta);
		int level = currentState.get(CauldronBlock.LEVEL);
		int toAdd = Math.min(Math.max(0, amount), 3 - level);
		BlockState newState = currentState.with(CauldronBlock.LEVEL, level + toAdd);
		ta.put(this.key, newState);
		return toAdd;
	}

	public int extract(Transaction ta, int amount) {
		BlockState currentState = this.key.getCurrentState(ta);
		int level = currentState.get(CauldronBlock.LEVEL);
		int toRemove = Math.min(Math.max(0, amount), level);
		BlockState newState = currentState.with(CauldronBlock.LEVEL, level - toRemove);
		ta.put(this.key, newState);
		return toRemove;
	}

	public int getCurrentAmount(Transaction ta) {
		BlockState currentState = this.key.getCurrentState(ta);
		return currentState.get(CauldronBlock.LEVEL);
	}

	public static CauldronTransactionSupport create(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() != Blocks.CAULDRON) return null;
		return new CauldronTransactionSupport(world, pos, state);
	}

	private static final class DataKey implements FullCopyDataKey<BlockState> {
		private final World world;
		private final BlockPos pos;
		private final BlockState state;

		private DataKey(World world, BlockPos pos, BlockState state) {
			this.world = world;
			this.pos = pos;
			this.state = state;
		}

		@Override
		public BlockState getPersistentState() {
			return this.state;
		}

		@Override
		public BlockState copy(BlockState value) {
			return value;
		}

		@Override
		public void applyChanges(BlockState changes) {
			this.world.setBlockState(this.pos, changes);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			DataKey dataKey = (DataKey) o;
			return world.equals(dataKey.world) &&
				pos.equals(dataKey.pos);
		}

		@Override
		public int hashCode() {
			return Objects.hash(world, pos);
		}
	}

}
