package net.fabricmc.fabric.impl.event;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.BlockAttackInteractionAware;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.util.ActionResult;

public class InteractionEventsRouter implements ModInitializer {
    @Override
    public void onInitialize() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            BlockState state = world.getBlockState(pos);
            if (state instanceof BlockAttackInteractionAware) {
                if (((BlockAttackInteractionAware) state).onAttackInteraction(state, world, pos, player, hand, direction)) {
                    return ActionResult.FAIL;
                }
            } else if (state.getBlock() instanceof BlockAttackInteractionAware) {
                if (((BlockAttackInteractionAware) state.getBlock()).onAttackInteraction(state, world, pos, player, hand, direction)) {
                    return ActionResult.FAIL;
                }
            }

            return ActionResult.PASS;
        });
    }
}
