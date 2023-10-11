package net.fabricmc.fabric.api.event.client.player;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Contains client side events triggered by block breaking.
 *
 * <p>For preventing block breaking client side and other purposes, see {@link net.fabricmc.fabric.api.event.player.AttackBlockCallback}.
 */
public class ClientPlayerBlockBreakEvents {
	/**
	 * Callback after a block is broken.
	 *
	 * <p>Only called client side.
	 */
	public static final Event<After> AFTER = EventFactory.createArrayBacked(After.class,
			(listeners) -> (world, player, pos, state, entity) -> {
				for (After event : listeners) {
					event.afterBlockBreak(world, player, pos, state, entity);
				}
			}
	);

	@FunctionalInterface
	public interface After {
		/**
		 * Called after a block is successfully broken.
		 *
		 * @param world the world where the block was broken
		 * @param player the player who broke the block
		 * @param pos the position where the block was broken
		 * @param state the block state <strong>before</strong> the block was broken
		 * @param blockEntity the block entity of the broken block, can be {@code null}
		 */
		void afterBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity);
	}
}
