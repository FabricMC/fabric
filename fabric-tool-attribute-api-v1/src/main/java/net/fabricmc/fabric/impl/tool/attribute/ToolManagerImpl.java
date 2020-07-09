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

package net.fabricmc.fabric.impl.tool.attribute;

import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.fabricmc.fabric.api.util.TriState;

public final class ToolManagerImpl {
	public interface Entry {
		void setBreakByHand(boolean value);

		void putBreakByTool(Tag<Item> tag, int miningLevel);

		int getMiningLevel(Tag<Item> tag);
	}

	private static class EntryImpl implements Entry {
		private Tag<Item>[] tags = new Tag[0];
		private int[] tagLevels = new int[0];
		private TriState defaultValue = TriState.DEFAULT;

		@Override
		public void setBreakByHand(boolean value) {
			this.defaultValue = TriState.of(value);
		}

		@Override
		public void putBreakByTool(Tag<Item> tag, int miningLevel) {
			tag(tag); // Generate tag entry

			for (int i = 0; i < tags.length; i++) {
				if (tags[i] == tag) {
					tagLevels[i] = miningLevel;
					return;
				}
			}

			tags = Arrays.copyOf(tags, tags.length + 1);
			tags[tags.length - 1] = tag;

			tagLevels = Arrays.copyOf(tagLevels, tagLevels.length + 1);
			tagLevels[tagLevels.length - 1] = miningLevel;
		}

		@Override
		public int getMiningLevel(Tag<Item> tag) {
			for (int i = 0; i < tags.length; i++) {
				if (tags[i] == tag) {
					return tagLevels[i];
				}
			}

			return -1;
		}
	}

	private static final Map<Tag<Item>, Event<ToolHandler>> HANDLER_MAP = new HashMap<>();
	private static final Event<ToolHandler> GENERAL_TOOLS_HANDLER = EventFactory.createArrayBacked(ToolHandler.class, ToolManagerImpl::toolHandlerInvoker);

	private static final Map<Block, EntryImpl> ENTRIES = new IdentityHashMap<>();

	/**
	 * Returns a event for the tag provided, creates a new event if it does not exist.
	 *
	 * @param tag the tag provided for the tool
	 * @return the event callback.
	 */
	public static Event<ToolHandler> tag(Tag<Item> tag) {
		for (Map.Entry<Tag<Item>, Event<ToolHandler>> entry : HANDLER_MAP.entrySet()) {
			if ((tag instanceof Tag.Identified ? (((Tag.Identified<Item>) entry.getKey()).getId().equals(((Tag.Identified<Item>) tag).getId())) : entry.getKey().equals(tag))) {
				return entry.getValue();
			}
		}

		HANDLER_MAP.put(tag, EventFactory.createArrayBacked(ToolHandler.class, ToolManagerImpl::toolHandlerInvoker));
		return HANDLER_MAP.get(tag);
	}

	/**
	 * Returns a event used for every tag registered.
	 *
	 * @return the event callback.
	 */
	public static Event<ToolHandler> general() {
		return GENERAL_TOOLS_HANDLER;
	}

	private static ToolHandler toolHandlerInvoker(ToolHandler[] toolHandlers) {
		return new ToolHandler() {
			@Override
			public ActionResult isEffectiveOn(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
				for (ToolHandler toolHandler : toolHandlers) {
					ActionResult effectiveOn = Objects.requireNonNull(toolHandler.isEffectiveOn(tag, state, stack, user));

					if (effectiveOn != ActionResult.PASS) {
						return effectiveOn;
					}
				}

				return ActionResult.PASS;
			}

			@Override
			public TypedActionResult<Float> getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
				for (ToolHandler toolHandler : toolHandlers) {
					TypedActionResult<Float> miningSpeedMultiplier = Objects.requireNonNull(toolHandler.getMiningSpeedMultiplier(tag, state, stack, user));

					if (miningSpeedMultiplier.getResult() != ActionResult.PASS) {
						return miningSpeedMultiplier;
					}
				}

				return TypedActionResult.pass(1f);
			}
		};
	}

	public static Entry entry(Block block) {
		return ENTRIES.computeIfAbsent(block, (bb) -> new EntryImpl());
	}

	public static Entry entryNullable(Block block) {
		return ENTRIES.get(block);
	}

	@Deprecated
	public static void registerBreakByHand(Block block, boolean value) {
		entry(block).setBreakByHand(value);
	}

	@Deprecated
	public static void registerBreakByTool(Block block, Tag<Item> tag, int miningLevel) {
		entry(block).putBreakByTool(tag, miningLevel);
	}

	/**
	 * Hook for ItemStack.isEffectiveOn and similar methods.
	 */
	//TODO: nullable on user once we have an official @Nullable annotation in
	public static boolean handleIsEffectiveOnIgnoresVanilla(BlockState state, ItemStack stack, LivingEntity user, boolean vanillaResult) {
		for (Map.Entry<Tag<Item>, Event<ToolHandler>> eventEntry : HANDLER_MAP.entrySet()) {
			if (stack.getItem().isIn(eventEntry.getKey())) {
				ActionResult effective = eventEntry.getValue().invoker().isEffectiveOn(eventEntry.getKey(), state, stack, user);
				if (effective.isAccepted()) return true;
				effective = general().invoker().isEffectiveOn(eventEntry.getKey(), state, stack, user);
				if (effective.isAccepted()) return true;
			}
		}

		EntryImpl entry = (EntryImpl) entryNullable(state.getBlock());

		return (entry != null && entry.defaultValue.get()) || (entry == null && vanillaResult);
	}

	public static float handleBreakingSpeedIgnoresVanilla(BlockState state, ItemStack stack, /* @Nullable */ LivingEntity user) {
		float breakingSpeed = 0f;
		Tag<Item> handledTag = null;
		boolean handled = false;

		for (Map.Entry<Tag<Item>, Event<ToolHandler>> eventEntry : HANDLER_MAP.entrySet()) {
			if (stack.getItem().isIn(eventEntry.getKey())) {
				TypedActionResult<Float> speedMultiplier = Objects.requireNonNull(eventEntry.getValue().invoker().getMiningSpeedMultiplier(eventEntry.getKey(), state, stack, user));

				if (speedMultiplier.getResult().isAccepted()) {
					handled = true;

					if (speedMultiplier.getValue() > breakingSpeed) {
						breakingSpeed = speedMultiplier.getValue();
						handledTag = eventEntry.getKey();
					}
				}

				speedMultiplier = Objects.requireNonNull(general().invoker().getMiningSpeedMultiplier(eventEntry.getKey(), state, stack, user));

				if (speedMultiplier.getResult().isAccepted()) {
					handled = true;

					if (speedMultiplier.getValue() > breakingSpeed) {
						breakingSpeed = speedMultiplier.getValue();
						handledTag = eventEntry.getKey();
					}
				}
			}
		}

		// Give it a default speed if it is not handled.
		breakingSpeed = handled ? breakingSpeed : 1f;

		if (stack.getItem() instanceof DynamicAttributeTool) {
			breakingSpeed = ((DynamicAttributeTool) stack.getItem()).postProcessMiningSpeed(handledTag, state, stack, user, breakingSpeed, handled);
		}

		return breakingSpeed;
	}

	/**
	 * The handler to handle tool speed and effectiveness.
	 *
	 * @see net.fabricmc.fabric.impl.tool.attribute.ToolHandlers for default handlers.
	 */
	public interface ToolHandler {
		/**
		 * Determines whether this handler is active and effective of the tools.
		 *
		 * @param tag   the tag involved
		 * @param state the block state to break
		 * @param stack the item stack breaking the block
		 * @param user  the user involved in breaking the block, null if not applicable.
		 * @return the result of effectiveness
		 */
		default ActionResult isEffectiveOn(Tag<Item> tag, BlockState state, ItemStack stack, /* @Nullable */ LivingEntity user) {
			return ActionResult.PASS;
		}

		/**
		 * Determines the mining speed multiplier of the tools.
		 *
		 * @param tag   the tag involved
		 * @param state the block state to break
		 * @param stack the item stack breaking the block
		 * @param user  the user involved in breaking the block, null if not applicable.
		 * @return the result of mining speed.
		 */
		default TypedActionResult<Float> getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			return null;
		}
	}
}
