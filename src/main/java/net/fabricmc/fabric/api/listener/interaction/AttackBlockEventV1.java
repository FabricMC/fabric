package net.fabricmc.fabric.api.listener.interaction;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@FunctionalInterface
public interface AttackBlockEventV1 {
	ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction);
}
