# Fabric Enchantment API (v1)

## Organization

This module is split into two parts. The first part is the `FabricEnchantment` class which allows mod developers to create enchantments with targets other than the ones provided by the vanilla game. The second part is the `EnchantmentEvents` class which allows developers to easily modify existing enchantment target logic on a case by case basis using callback events.

# Fabric Enchantment

`FabricEnchantment` is an abstract class that modded enchantments are encouraged to extend from, which provides developers greater flexibility in defining which items ought to be able to accept the enchantment by breaking away from the vanilla enchantment target system.

By extending `FabricEnchantment` developers are opting into the greater flexibility that the associated mixins provide in exchange for having to ditch the vanilla enchantment target system.

## Methods

`FabricEnchantment` defines three methods to provide developers greater flexibility.

#### `FabricEnchantment.isEnchantableItem`

This method allows developers to determine whether the given enchantment should be applicable to the given item stack in the enchantment table. By default it delegates to `FabricEnchantment.isAcceptableItem` meaning all items that can accept the enchantment in the anvil can also accept the enchantment in the enchantment table.

#### `FabricEnchantment.isAcceptableItem`

This method allows developers to determine whether the given enchantment should be applicable to the given item stack in the anvil. By default `FabricEnchantment.isEnchantableItem` delegates to this method meaning that this method also determines the logic for the enchantment table unless `FabricEnchantment.isEnchantableItem` is overridden.

#### `FabricEnchantment.isAcceptableItemGroup`

This method allows developers to determine whether their custom enchanted books should be added into the given item group.

In vanilla the item group of enchanted books is determined by the enchantment target, so without an enchantment target the enchantment itself needs a method determining if it should be added or not.

# Enchantment Events

#### `EnchantmentEvents.ACCEPT_ENCHANTMENT`
 - Called when determining whether an enchantment can be added to an item stack in the enchantment table
 - Mods looking to allow their custom items to accept a vanilla enchantment can use this event
    - As an example, the test mod allows diamond shovels to be enchanted with flame

#### `EnchantmentEvents.ACCEPT_APPLICATION`
 - Called when determining whether an enchantment should be applied to an item stack using the anvil
 - Mods looking to allow vanilla enchantment to be applied to their custom items can use this event to do so
