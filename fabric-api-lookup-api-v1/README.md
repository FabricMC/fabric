#  Fabric Api Lookup API (v1)
This module allows Api instances to be associated with game objects without specifying how the association is implemented.
This is useful when the same Api could be implemented more than once or implemented in different ways.

* What we call an Api is any object that can be offered or queried, possibly by different mods, to be used in an agreed-upon manner.
* In this module, such objects are represented by the generic type `T`.
  This module allows flexible retrieving of such Apis from blocks in the world.
* It also provides building blocks for defining ways of retrieving Apis from other game objects.

# Retrieving Apis from blocks
## `BlockApiLookup`
The primary way of querying Api instances for blocks in the world.
It exposes a `get` function to retrieve an Api instance, and multiple `register*` functions to register Apis for blocks and block entities.

The javadoc of this class contains a full usage example.

## `BlockApiLookupRegistry`
Provides access to `BlockApiLookup` instances.

## `BlockApiCache`
A `BlockApiLookup` bound to a position and a server world, allowing much faster repeated Api queries.

# Retrieving Apis from other game objects
## `ApiLookupMap`
A map to store `*ApiLookup`s, allowing easy implementations of `*ApiLookupRegistry`s.

## `ApiProviderMap`
A copy-on-write map to store Api providers, for easy and efficient implementations of `*ApiLookup`s.
