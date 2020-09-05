# Coding Conventions

The following document outlines a series of conventions that must be used for all code contributions to Fabric API, and recommendations for other modding projects.

## General

All code should meet the Yarn naming conventions which can be found [here](https://github.com/FabricMC/yarn/blob/20w21a/CONVENTIONS.md).

All calls of associated methods and uses of class fields should be prefixed with `this.` For example, setting the field `name` on a class should always be done by calling `this.name = "name"` rather than `name = "name"`. This is especially pertinent in constructors where constructor parameters often share a name with class fields.

## Annotations

All methods that can potentially return a null value should be annotated in-line using the JetBrains `@Nullable` right before the return type definition for the method. For example, a method named `possiblyGetName` that can potentially return a null value should be written as `public @Nullable String possiblyGetName()...`.

Likewise all methods that can accept a null value should be annotated using the JetBrains `@Nullable` right before the method parameter. For example, a method named `food` that can accept a null value should be written as follows `public Item.Settings food(@Nullable FoodComponent food)...`.

## Flow Control

Wherever it makes sense the ternary operator ought to be preferred over an if statement for the sake of brevity, but never at the expense of readability. If the statement contains a long if condition it should not be turned into a ternary operator because the line will be too long and the statement will lose readability. If the statement contains a short if else statement and can be expressed simply as a ternary operator, that ought to be preferred.

## Enums

Enum values that are referenced internally (values referenced within methods associated with their own enum definition) must always use `Enum.VALUE` never simply `VALUE`.

## Mixins

Regular mixin classes must be named the same name as the target class they are being mixed into, with "Mixin" appended to the end of the class name. As an example, a mixin class into the class `MinecraftServer` should be named `MinecraftServerMixin`.

Accessor mixin classes must be named the same name as the target class they are being mixed into, with "Accessor" appended to the end of the class name. As an example, the accessor class into the class `MinecraftServer` should be named `MinecraftServerAccessor`.

"Duck interfaces" (Interfaces that are implemented onto classes using mixins) should be named the same name as the target class that implements the interface, with a series of adjectives at the front of the class name describing the contents or the implementation use case of the interface, along with "Hooks" at the end of the class name. For example, a duck interface implemented by `PlayerEntity` through mixins in order to add a mana player attribute should be named `ManaPlayerEntityHooks`.

Mixins that shadow final fields must use the `@Final` annotation provided by the mixin library.

Mixin injection and redirect methods must be named based upon the specific function of the injection or redirection, not the method that they are injecting into or redirecting. For example a method inject that registers commands on the dedicated server ought to be named `registerCommands` not the name of the target method, in this case `setupServer`.

If a shadowed method needs to be prefixed because of a conflict it must use the prefix `minecraft$` for methods in minecraft, `[namespace]$` for projects with a different namespace (which should always match the mod id), or if neither of those are available `[package name]$`. Take for example a situation where the shadowed method `fromJson` conflicts with a new `fromJson` method. The shadowed method should be renamed to `minecraft$fromJson` and annotated as follows `@Shadow(prefix = "minecraft$")`.

All mixin classes soft implementing interfaces must use the prefix `fabric$` and the `@Implements` annotation from the mixin library. For example, take a class that soft implements the interface `Identifiable`, it must be annotated as follows `@Implements(@Interface(iface = Identifiable.class, prefix = "fabric$"))`.

## Project Structure

Modules that do not contain API (API in data form is still considered API, such as tags) should not have an `-api` suffix.

Event-based and registry-based modules must use the `-events` and `-registries` suffixes, respectively. These are API, however the `-api` suffix is redundant because events and registries are inherently API.

Modules names will always be written in plain, grammatical, U.S. English. In English, adjectives come before nouns so, as an example, modules should be named `fabric-indigo-renderer` instead of such as `fabric-renderer-indigo`.

Every name consisting of multiple words must have a hypen (-) between the words. This includes class names such as BlockEntity being represented as `block-entity`.

Packages must follow the following format (using `fabric-biome-api-v1` as an example). API Packages must use the root `net.fabricmc.fabric.api.biome.v1`. Implementation Packages must use the root `net.fabricmc.fabric.impl.biome`. Mixin Packages must use the root `net.fabricmc.fabric.mixin.biome`.

Multi-word names can be split or combined depending on what makes sense from a hierarchical standpoint. Take for example a module named `fabric-lifecycle-events-v1`. It would have the root package `net.fabricmc.fabric.api.event.lifecycle.v1` to maintain the hierarchy of lifecycle being a type of event. A module named `fabric-networking-block-entity-v1` would have the root package `net.fabricmc.fabric.api.networking.blockentity`, because "Block Entity" is a singular construct under "networking", and should not imply that entity was hierarchically inferior to block.

Experimental apis in Fabric API ought to be annotated using `@Deprecated` in order to indicate instability and the potential for the api to drastically change between versions.
