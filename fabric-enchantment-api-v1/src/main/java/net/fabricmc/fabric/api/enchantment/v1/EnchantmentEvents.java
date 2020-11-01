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

package net.fabricmc.fabric.api.enchantment.v1;

import java.util.function.BiFunction;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.util.TriState;

/**
 * Events to intercept the vanilla enchantment logic.
 *
 * @author Vaerian (vaeriann@gmail.com or @Vaerian on GitHub).
 *
 * <p>Please contact the author, Vaerian, at the email or GitHub profile listed above
 * with any questions surrounding implementation choices, functionality, or updating
 * newer versions of the game.</p>
 */
public final class EnchantmentEvents {
	/**
	 * Overrides whether or not the given enchantment should be applied to the given item
	 * only when enchanted using an anvil.
	 *
	 * <p>Callbacks are evaluated on a first come, first serve basis where callbacks
	 * registered earlier will have functional priority over ones registered later.</p>
	 */
	public static final Event<AcceptApplication> ACCEPT_APPLICATION = EventFactory.createArrayBacked(AcceptApplication.class, (callbacks) -> (enchantment, stack) -> {
		for (AcceptApplication callback : callbacks) {
			TriState result = callback.shouldAccept(enchantment, stack);

			if (result != TriState.DEFAULT) {
				return result;
			}
		}

		return TriState.DEFAULT;
	});

	/**
	 * Overrides whether or not the given enchantment should be applied to the given item
	 * only when enchanted using an enchantment table.
	 *
	 * <p>Callbacks are evaluated on a first come, first serve basis where callbacks
	 * registered earlier will have functional priority over ones registered later.</p>
	 */
	public static final Event<AcceptEnchantment> ACCEPT_ENCHANTMENT = EventFactory.createArrayBacked(AcceptEnchantment.class, (callbacks) -> (enchantment, stack) -> {
		for (AcceptEnchantment callback : callbacks) {
			TriState result = callback.shouldAccept(enchantment, stack);

			if (result != TriState.DEFAULT) {
				return result;
			}
		}

		return TriState.DEFAULT;
	});

	/**
	 * Simple helper method to add an enchantment callback to both the enchantment and application
	 * events.
	 *
	 * @param callback The enchantment callback to add to both enchantment events.
	 */
	public static void registerAll(BiFunction<Enchantment, ItemStack, TriState> callback) {
		ACCEPT_ENCHANTMENT.register((AcceptEnchantment) callback);
		ACCEPT_APPLICATION.register((AcceptApplication) callback);
	}

	public interface AcceptEnchantment {
		/**
		 * Determines whether or not the given enchantment should be applied to the given item stack
		 * using an enchantment table.
		 *
		 * <p>Returning {@link net.fabricmc.fabric.api.util.TriState#DEFAULT} will delegate to the
		 * vanilla/modded default functionality for the enchantment. Returning
		 * {@link net.fabricmc.fabric.api.util.TriState#TRUE} will override the default functionality
		 * and force the item to accept the enchantment. Returning
		 * {@link net.fabricmc.fabric.api.util.TriState#FALSE} will override the default functionality
		 * and prevent the item from accepting the enchantment.</p>
		 *
		 * @param enchantment The enchantment looking to be applied.
		 * @param stack The item stack looking to accept the given enchantment.
		 * @return Whether or not the given enchantment should be applied to the given item stack
		 * through the use of an enchantment table.
		 */
		TriState shouldAccept(Enchantment enchantment, ItemStack stack);
	}

	public interface AcceptApplication {
		/**
		 * Determines whether or not the given enchantment should be applied to the given item stack
		 * using an anvil.
		 *
		 * <p>Returning {@link net.fabricmc.fabric.api.util.TriState#DEFAULT} will delegate to the
		 * vanilla/modded default functionality for the enchantment. Returning
		 * {@link net.fabricmc.fabric.api.util.TriState#TRUE} will override the default functionality
		 * and force the item to accept the enchantment. Returning
		 * {@link net.fabricmc.fabric.api.util.TriState#FALSE} will override the default functionality
		 * and prevent the item from accepting the enchantment.</p>
		 *
		 * @param enchantment The enchantment looking to be applied.
		 * @param stack The item stack looking to accept the given enchantment.
		 * @return Whether or not the given enchantment should be applied to the given item stack
		 * through the use of an anvil.
		 */
		TriState shouldAccept(Enchantment enchantment, ItemStack stack);
	}
}
