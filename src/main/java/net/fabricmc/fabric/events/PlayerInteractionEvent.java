/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.events;

import net.fabricmc.fabric.util.HandlerList;
import net.fabricmc.fabric.util.HandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Facing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * This is a class for INTERACTION EVENTS (think left-clicking/right-clicking). For block placement/break
 * events, look elsewhere - this just handles the interaction!
 *
 * These hook in BEFORE the spectator checks, so make sure to check for the player's game mode as well!
 *
 * CURRENT LIMITATIONS:
 *
 * - INTERACT_BLOCK/INTERACT_ITEM do not expect the ItemStack instance in the player's held hand to change!
 *   If you must do that, consider returning an ActionResult.SUCCESS and re-emitting the event in some manner!
 */
public final class PlayerInteractionEvent {
	@FunctionalInterface
	public interface Block {
		ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos pos, Facing facing);
	}

	@FunctionalInterface
	public interface Entity {
		ActionResult interact(PlayerEntity player, World world, Hand hand, net.minecraft.entity.Entity entity);
	}

	@FunctionalInterface
	public interface BlockPositioned {
		ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos pos, Facing facing, float hitX, float hitY, float hitZ);
	}

	@FunctionalInterface
	public interface EntityPositioned {
		ActionResult interact(PlayerEntity player, World world, Hand hand, net.minecraft.entity.Entity entity, Vec3d hitPosition);
	}

	@FunctionalInterface
	public interface Item {
		ActionResult interact(PlayerEntity player, World world, Hand hand);
	}

	/**
	 * Event emitted when a player "attacks" a block.
	 */
	public static final HandlerRegistry<Block> ATTACK_BLOCK = new HandlerList<>(Block.class);

	/**
	 * Event emitted when a player "attacks" an entity.
	 */
	public static final HandlerRegistry<Entity> ATTACK_ENTITY = new HandlerList<>(Entity.class);
	
	// TODO: For completeness' sake, but requires us to add a custom packet. Is it worth the complexity?
	/* public static final HandlerRegistry<Item> ATTACK_ITEM = new HandlerList<>(); */

	/**
	 * Event emitted when a player interacts with a block.
	 */
	public static final HandlerRegistry<BlockPositioned> INTERACT_BLOCK = new HandlerList<>(BlockPositioned.class);

	/**
	 * Event emitted when a player interacts with an entity.
	 *
	 * Developer note: Minecraft provides two methods to interact with
	 * Entities - one takes in a hit position, the other does not. However,
	 * all vanilla interaction cases seem to use one, then the other - as such,
	 * only one event is currently provided, but it is accordingly named in
	 * the case of a second event being necessary.
	 */
	public static final HandlerRegistry<EntityPositioned> INTERACT_ENTITY_POSITIONED = new HandlerList<>(EntityPositioned.class);

	/**
	 * Event emitted when a player interacts with an item.
	 */
	public static final HandlerRegistry<Item> INTERACT_ITEM = new HandlerList<>(Item.class);

	/**
	 * @deprecated Use {@link #ATTACK_BLOCK ATTACK_BLOCK} instead.
	 */
	@SuppressWarnings("DeprecatedIsStillUsed")
	@Deprecated
	public static final HandlerRegistry<Block> BREAK_BLOCK = ATTACK_BLOCK;

	private PlayerInteractionEvent() {

	}
}
