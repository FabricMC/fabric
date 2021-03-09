#  Fabric API Lookup API (v1)
This module allows API instances to be associated with game objects without specifying how the association is implemented.
This is useful when the same API could be implemented more than once or implemented in different ways.
See also the [package-info.java file](src/main/java/net/fabricmc/fabric/api/lookup/v1/package-info.java).

* What we call an API is any object that can be offered or queried, possibly by different mods, to be used in an agreed-upon manner.
* This module allows flexible retrieving of such APIs, represented by the generic type `A`, from blocks in the world or from item stacks.
* It also provides building blocks for defining custom ways of retrieving APIs from other game objects.

# Retrieving APIs from blocks
See the javadoc of `BlockApiLookup` for a full usage example.

## [`BlockApiLookup`](src/main/java/net/fabricmc/fabric/api/lookup/v1/block/BlockApiLookup.java)
The primary way of querying API instances for blocks in the world.
It exposes a `find` function to retrieve an API instance, and multiple `register*` functions to register APIs for blocks and block entities.

Instances can be obtained using the `get` function.

## [`BlockApiCache`](src/main/java/net/fabricmc/fabric/api/lookup/v1/block/BlockApiCache.java)
A `BlockApiLookup` bound to a position and a server world, allowing much faster repeated API queries.

# Retrieving APIs from items
See the javadoc of `ItemApiLookup` for a full usage example.

## [`ItemApiLookup`](src/main/java/net/fabricmc/fabric/api/lookup/v1/item/ItemApiLookup.java)
The way to query API instances from item stacks.
It exposes a `find` function to retrieve an API instance, and multiple `register*` functions to register APIs for items.

# Retrieving APIs from custom objects
The subpackage `custom` provides helper classes to accelerate implementations of `ApiLookup`s for custom objects,
similar to the existing `BlockApiLookup`, but with different query parameters.

## [`ApiLookupMap`](src/main/java/net/fabricmc/fabric/api/lookup/v1/custom/ApiLookupMap.java)
A map meant to be used as the backing storage for custom `ApiLookup` instances, to implement a custom equivalent of `BlockApiLookup#get`.

## [`ApiProviderMap`](src/main/java/net/fabricmc/fabric/api/lookup/v1/custom/ApiProviderMap.java)
A fast thread-safe copy-on-write map meant to be used as the backing storage for registered providers.
