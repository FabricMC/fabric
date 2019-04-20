package net.fabricmc.fabric.impl.event;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.block.BlockPickInteractionAware;
import net.fabricmc.fabric.api.entity.EntityPickInteractionAware;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockGatherCallback;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class InteractionEventsRouterClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPickBlockGatherCallback.EVENT.register(((player, result) -> {
            if (result instanceof BlockHitResult) {
                BlockView view = player.getEntityWorld();
                BlockPos pos = ((BlockHitResult) result).getBlockPos();
                BlockState state = view.getBlockState(pos);

                if (state.getBlock() instanceof BlockPickInteractionAware) {
                    return (((BlockPickInteractionAware) state.getBlock()).getPickedStack(state, view, pos, player, result));
                }
            } else if (result instanceof EntityHitResult) {
                Entity entity = ((EntityHitResult) result).getEntity();

                if (entity instanceof EntityPickInteractionAware) {
                    return ((EntityPickInteractionAware) entity).getPickedStack(player, result);
                }
            }

            return ItemStack.EMPTY;
        }));
    }
}
