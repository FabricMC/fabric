/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
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

package net.fabricmc.fabric.test.entity.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

public final class EntityEventTests implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger(EntityEventTests.class);
	public static final Block TEST_BED = new TestBedBlock(AbstractBlock.Settings.of(Material.WOOL).strength(1, 1));

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier("fabric-entity-events-v1-testmod", "test_bed"), TEST_BED);
		Registry.register(Registry.ITEM, new Identifier("fabric-entity-events-v1-testmod", "test_bed"), new BlockItem(TEST_BED, new Item.Settings().group(ItemGroup.DECORATIONS)));

		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killed) -> {
			LOGGER.info("Entity Killed: {}", killed);
		});

		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
			LOGGER.info("Moved player {}: [{} -> {}]", player, origin.getRegistryKey().getValue(), destination.getRegistryKey().getValue());
		});

		ServerEntityWorldChangeEvents.AFTER_ENTITY_CHANGE_WORLD.register((originalEntity, newEntity, origin, destination) -> {
			LOGGER.info("Moved entity {} -> {}: [({} -> {}]", originalEntity, newEntity, origin.getRegistryKey().getValue(), destination.getRegistryKey().getValue());
		});

		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			LOGGER.info("Copied data for {} from {} to {}", oldPlayer.getGameProfile().getName(), oldPlayer, newPlayer);
		});

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			LOGGER.info("Respawned {}, [{}, {}]", oldPlayer.getGameProfile().getName(), oldPlayer.getServerWorld().getRegistryKey().getValue(), newPlayer.getServerWorld().getRegistryKey().getValue());
		});

		ServerPlayerEvents.ALLOW_DEATH.register((player, source, amount) -> {
			LOGGER.info("{} is going to die to {} damage from {} damage source", player.getGameProfile().getName(), amount, source.getName());

			if (player.getStackInHand(Hand.MAIN_HAND).getItem() == Items.APPLE) {
				player.setHealth(3.0f);
				return false;
			}

			return true;
		});

		EntitySleepEvents.ALLOW_SLEEPING.register((player, sleepingPos) -> {
			// Can't sleep if holds blue wool
			if (player.getStackInHand(Hand.MAIN_HAND).getItem() == Items.BLUE_WOOL) {
				return PlayerEntity.SleepFailureReason.OTHER_PROBLEM;
			}

			return null;
		});

		EntitySleepEvents.START_SLEEPING.register((entity, sleepingPos) -> {
			LOGGER.info("Entity {} sleeping at {}", entity, sleepingPos);
		});

		EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> {
			LOGGER.info("Entity {} woke up at {}", entity, sleepingPos);
		});

		EntitySleepEvents.ALLOW_BED.register((entity, sleepingPos, state, vanillaResult) -> {
			return state.isOf(TEST_BED) ? ActionResult.SUCCESS : ActionResult.PASS;
		});

		EntitySleepEvents.MODIFY_SLEEPING_DIRECTION.register((entity, sleepingPos, sleepingDirection) -> {
			return entity.world.getBlockState(sleepingPos).isOf(TEST_BED) ? Direction.NORTH : sleepingDirection;
		});

		EntitySleepEvents.ALLOW_SLEEP_TIME.register((player, sleepingPos, vanillaResult) -> {
			// Yellow wool allows to sleep during the day
			if (player.world.isDay() && player.getStackInHand(Hand.MAIN_HAND).getItem() == Items.YELLOW_WOOL) {
				return ActionResult.SUCCESS;
			}

			return ActionResult.PASS;
		});

		EntitySleepEvents.ALLOW_NEARBY_MONSTERS.register((player, sleepingPos, vanillaResult) -> {
			// Green wool allows monsters and red wool always "detects" monsters
			ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);

			if (stack.getItem() == Items.GREEN_WOOL) {
				return ActionResult.SUCCESS;
			} else if (stack.getItem() == Items.RED_WOOL) {
				return ActionResult.FAIL;
			}

			return ActionResult.PASS;
		});

		EntitySleepEvents.ALLOW_SETTING_SPAWN.register((player, sleepingPos) -> {
			// Don't set spawn if holding white wool
			return player.getStackInHand(Hand.MAIN_HAND).getItem() != Items.WHITE_WOOL;
		});

		EntitySleepEvents.ALLOW_RESETTING_TIME.register(player -> {
			// Don't allow resetting time if holding black wool
			return player.getStackInHand(Hand.MAIN_HAND).getItem() != Items.BLACK_WOOL;
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(CommandManager.literal("addsleeptestwools").executes(context -> {
				addSleepWools(context.getSource().getPlayer());
				return 0;
			}));
		});
	}

	private static void addSleepWools(PlayerEntity player) {
		PlayerInventory inventory = player.inventory;
		inventory.offerOrDrop(player.world, createNamedItem(Items.BLUE_WOOL, "Can't start sleeping"));
		inventory.offerOrDrop(player.world, createNamedItem(Items.YELLOW_WOOL, "Sleep whenever"));
		inventory.offerOrDrop(player.world, createNamedItem(Items.GREEN_WOOL, "Allow nearby monsters"));
		inventory.offerOrDrop(player.world, createNamedItem(Items.RED_WOOL, "Detect nearby monsters"));
		inventory.offerOrDrop(player.world, createNamedItem(Items.WHITE_WOOL, "Don't set spawn"));
		inventory.offerOrDrop(player.world, createNamedItem(Items.BLACK_WOOL, "Don't reset time"));
	}

	private static ItemStack createNamedItem(Item item, String name) {
		ItemStack stack = new ItemStack(item);
		stack.setCustomName(new LiteralText(name));
		return stack;
	}
}
