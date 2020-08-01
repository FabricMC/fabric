package net.fabricmc.fabric.api.event.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Callback for when a block is broken.
 * Only called on the server, however updates are synced with the client.
 *
 * <p>Upon return:
 * <ul><li>SUCCESS/PASS/CONSUME continues the default code for breaking the block
 * <li>FAIL cancels the block breaking action
 */
public interface BreakBlockCallback {
	Event<BreakBlockCallback> EVENT = EventFactory.createArrayBacked(BreakBlockCallback.class,
			(listeners) -> (pos, state, entity, block) -> {
				for (BreakBlockCallback event : listeners) {
					ActionResult result = event.interact(pos, state, entity, block);

					if (result != ActionResult.PASS) {
						return result;
					}
				}

				return ActionResult.PASS;
			}
	);

	ActionResult interact(BlockPos pos, BlockState state, BlockEntity entity, Block block);
}
