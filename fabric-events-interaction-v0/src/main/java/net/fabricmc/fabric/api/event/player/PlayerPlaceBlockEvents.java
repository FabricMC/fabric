package net.fabricmc.fabric.api.event.player;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class PlayerPlaceBlockEvents {
	public static final Event<Before> BEFORE = EventFactory.createArrayBacked(Before.class,
			(listeners) -> ((context) -> {
				for (Before listener : listeners) {
					ActionResult result = listener.beforeBlockPlace(context);
					if(result != ActionResult.PASS) {
						return result;
					}
				}
				return ActionResult.PASS;
			})
	);

	public static final Event<After> AFTER = EventFactory.createArrayBacked(After.class,
			(listeners) -> ((world, entity, pos, state) -> {
				for (After listener : listeners) {
					listener.afterBlockPlace(world, entity, pos, state);
				}
			})
	);

	public static final Event<Cancelled> CANCELLED = EventFactory.createArrayBacked(Cancelled.class,
			(listeners) -> ((world, entity, pos, state) -> {
				for (Cancelled listener : listeners) {
					listener.cancelledBlockPlace(world, entity, pos, state);
				}
			})
	);

	public static final Event<Allow> ALLOW = EventFactory.createArrayBacked(Allow.class,
			(listeners) -> ((entity) -> {
				for (Allow listener : listeners) {
					if(!listener.allowBlockPlace(entity)) {
						return false;
					}
				}
				return true;
			})
	);

	@FunctionalInterface
	public interface Before {
		ActionResult beforeBlockPlace(ItemPlacementContext context);
	}

	@FunctionalInterface
	public interface After {
		void afterBlockPlace(World world, LivingEntity entity, BlockPos pos, BlockState state);
	}

	@FunctionalInterface
	public interface Cancelled {
		void cancelledBlockPlace(World world, LivingEntity entity, BlockPos pos, BlockState state);
	}

	@FunctionalInterface
	public interface Allow {
		boolean allowBlockPlace(LivingEntity entity);
	}
}
