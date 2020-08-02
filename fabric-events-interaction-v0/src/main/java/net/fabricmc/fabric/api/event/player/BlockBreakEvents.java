package net.fabricmc.fabric.api.event.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public class BlockBreakEvents {
	/**
	 * Callback before a block is broken.
	 * Only called on the server, however updates are synced with the client.
	 *
	 * <p>Upon return:
	 * <ul><li>SUCCESS/PASS/CONSUME continues the default code for breaking the block
	 * <li>FAIL cancels the block breaking action</ul>
	 */
	public static final Event<BeforeBreakBlockCallback> BEFORE = EventFactory.createArrayBacked(BeforeBreakBlockCallback.class,
			(listeners) -> (player, pos, state, entity, block) -> {
				for (BeforeBreakBlockCallback event : listeners) {
					ActionResult result = event.beforeBlockBreak(player, pos, state, entity, block);

					if (result != ActionResult.FAIL) {
						return result;
					}
				}

				return ActionResult.FAIL;
			}
	);

	/**
	 * Callback after a block is broken.
	 * Called on both Client and Server
	 */
	public static final Event<AfterBreakBlockCallback> AFTER = EventFactory.createArrayBacked(AfterBreakBlockCallback.class,
			(listeners) -> (player, pos, state, entity, block) -> {
				for (AfterBreakBlockCallback event : listeners) {
					event.afterBlockBreak(player, pos, state, entity, block);
				}
			}
	);

	@FunctionalInterface
	public interface BeforeBreakBlockCallback {
		ActionResult beforeBlockBreak(PlayerEntity player, BlockPos pos, BlockState state, /* Nullable */ BlockEntity entity, Block block);
	}

	@FunctionalInterface
	public interface AfterBreakBlockCallback {
		void afterBlockBreak(PlayerEntity player, BlockPos pos, BlockState state, /* Nullable */ BlockEntity entity, Block block);
	}
}
