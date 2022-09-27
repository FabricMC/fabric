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

package net.fabricmc.fabric.api.client.itemgroup;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;

/**
 * A builder for {@link ItemGroup}. Item groups are used to group items in the creative
 * inventory.
 *
 * <p>Example of creating an empty group (recommended for modded item-only group):</p>
 * <pre>{@code
 * MY_GROUP = FabricItemGroupBuilder.build(
 * 	new Identifier("my_mod", "example_group"),
 * 	() -> new ItemStack(MY_ITEM);
 * );
 * // Use item settings to assign a group. Otherwise, it won't appear on creative inventory
 * // search result.
 * MY_ITEM = new Item(new Item.Settings().group(MY_GROUP));
 * }</pre>
 *
 * <p>Example of creating a group with vanilla item stacks:</p>
 * <pre>{@code
 * ItemGroup myGroup = FabricItemGroupBuilder.create(new Identifier("my_mod", "group_2"))
 * 	.icon(Items.STONE::getDefaultStack)
 * 	.appendItems((stacks) -> {
 * 	   stacks.add(new ItemStack(Items.STONE));
 * 	   stacks.add(new ItemStack(Items.COBBLESTONE));
 * 	})
 * 	.build();
 * }</pre>
 *
 * <h2 id="search">Creative inventory searching</h2>
 * <p>Creative inventory search is implemented as a special item group. Adding items with
 * the builder by default does not add them to the search result; to make the item searchable,
 * set the item group via {@link Item.Settings#group(ItemGroup)} as well. If there are
 * multiple searchable item stacks of the item (such as colored variants or ones with
 * different NBT), override {@link Item#appendStacks(ItemGroup, DefaultedList)} on your
 * item as well. This should be called by {@link #appendItems(BiConsumer)} to actually add
 * items to your item group.</p>
 */
public final class FabricItemGroupBuilder {
	private final Identifier identifier;
	private Supplier<ItemStack> stackSupplier = () -> ItemStack.EMPTY;
	private BiConsumer<List<ItemStack>, ItemGroup> stacksForDisplay;

	private FabricItemGroupBuilder(Identifier identifier) {
		Objects.requireNonNull(identifier, "identifier cannot be null");
		this.identifier = identifier;
	}

	/**
	 * Creates a new item group builder.
	 *
	 * @param identifier the id of the ItemGroup, to be used as the translation key
	 * @return a new builder
	 */
	public static FabricItemGroupBuilder create(Identifier identifier) {
		return new FabricItemGroupBuilder(identifier);
	}

	/**
	 * Sets an icon for the group. This is displayed on the creative inventory tab.
	 *
	 * @param stackSupplier the supplier that returns the item stack to be used as an icon
	 * @return this builder
	 */
	public FabricItemGroupBuilder icon(Supplier<ItemStack> stackSupplier) {
		Objects.requireNonNull(stackSupplier, "icon cannot be null");
		this.stackSupplier = stackSupplier;
		return this;
	}

	/**
	 * This allows for a custom list of items to be displayed in a tab, this enabled tabs to be created with a custom set of items.
	 *
	 * @param appender Add ItemStack's to this list to show in the ItemGroup
	 * @return a reference to the FabricItemGroupBuilder
	 * @deprecated use {@link FabricItemGroupBuilder#appendItems(Consumer)}
	 */
	@Deprecated
	public FabricItemGroupBuilder stacksForDisplay(Consumer<List<ItemStack>> appender) {
		return appendItems(appender);
	}

	/**
	 * Sets the item stacks of this item group, by having the consumer add them to the passed list.
	 * This bypasses {@link Item#appendStacks}, and by default, does not append the stack to the search result.
	 * To add modded items, consider setting the group via {@linkplain Item.Settings#group(ItemGroup)
	 * item groups} in addition to this, as that adds the item stack to the search result.
	 * See the <a href="#search">creative inventory searching</a> section for details.
	 *
	 * <p>Calling this multiple times overwrites the previously set stacks.</p>
	 *
	 * @param stacksForDisplay a callback that adds item stacks to the passed list
	 * @return this builder
	 */
	public FabricItemGroupBuilder appendItems(Consumer<List<ItemStack>> stacksForDisplay) {
		return appendItems((itemStacks, itemGroup) -> stacksForDisplay.accept(itemStacks));
	}

	/**
	 * Sets the item stacks of this item group, by having the consumer add them to the passed list.
	 * This bypasses {@link Item#appendStacks}, and by default, does not append the stack to the search result.
	 * To add modded items, consider setting the group via {@linkplain Item.Settings#group(ItemGroup)
	 * item groups} in addition to this, as that adds the item stack to the search result.
	 * See the <a href="#search">creative inventory searching</a> section for details.
	 *
	 * <p>Compared to the other overload, this one also passes the new ItemGroup.
	 * This allows you to call {@link Item#appendStacks} yourself if you want.</p>
	 *
	 * <p>Calling this multiple times overwrites the previously set stacks.</p>
	 *
	 * @param stacksForDisplay a callback that adds item stacks to the passed list
	 * @return this builder
	 */
	public FabricItemGroupBuilder appendItems(BiConsumer<List<ItemStack>, ItemGroup> stacksForDisplay) {
		this.stacksForDisplay = stacksForDisplay;
		return this;
	}

	/**
	 * This is a single method that makes creating an empty ItemGroup with an icon one call.
	 * Items should be added using {@linkplain Item.Settings#group(ItemGroup) item settings}.
	 * Useful for modded item-only group.
	 *
	 * @param identifier    the id of the ItemGroup, to be used as the translation key
	 * @param stackSupplier the supplier that returns the item stack to be used as an icon
	 * @return an instance of the built ItemGroup
	 */
	public static ItemGroup build(Identifier identifier, Supplier<ItemStack> stackSupplier) {
		return new FabricItemGroupBuilder(identifier).icon(stackSupplier).build();
	}

	/**
	 * Creates an instance of the ItemGroup.
	 *
	 * @return an instance of the built ItemGroup
	 */
	public ItemGroup build() {
		((ItemGroupExtensions) ItemGroup.BUILDING_BLOCKS).fabric_expandArray();
		return new ItemGroup(ItemGroup.GROUPS.length - 1, String.format("%s.%s", identifier.getNamespace(), identifier.getPath())) {
			@Override
			public ItemStack createIcon() {
				return stackSupplier.get();
			}

			@Override
			public void appendStacks(DefaultedList<ItemStack> stacks) {
				if (stacksForDisplay != null) {
					stacksForDisplay.accept(stacks, this);
					return;
				}

				super.appendStacks(stacks);
			}
		};
	}
}
