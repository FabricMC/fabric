# Fabric Transfer API (v1)
This module provides common facilities for the transfer of fluids and other game resources.

## Transactions
The [`Transaction`](src/main/java/net/fabricmc/fabric/api/transfer/v1/transaction/Transaction.java) system provides a
scope that can be used to simulate any number of transfer operations, and then cancel or validate all of them at once.
One can think of transactions as video game checkpoints. A more detailed explanation can be found in the class javadoc of `Transaction`.
Every transfer operation requires a `Transaction` parameter.
[`SnapshotParticipant`](src/main/java/net/fabricmc/fabric/api/transfer/v1/transaction/base/SnapshotParticipant.java)
is the reference implementation of a "participant", that is an object participating in a transaction.

## Storages
A [`Storage<T>`](src/main/java/net/fabricmc/fabric/api/transfer/v1/storage/Storage.java) is any object that can store resources of type `T`.
Its contents can be read, and resources can be inserted into it or extracted from it.
[`StorageUtil`](src/main/java/net/fabricmc/fabric/api/transfer/v1/storage/StorageUtil.java) provides a few helpful functions to work with `Storage`s,
for example to move resources between two `Storage`s.
The [`storage/base`](src/main/java/net/fabricmc/fabric/api/transfer/v1/storage/base) package provides a few helpers to accelerate
implementation of `Storage<T>`.

Implementors of inventories with a fixed number of "slots" or "tanks" can use
[`SingleVariantStorage`](src/main/java/net/fabricmc/fabric/api/transfer/v1/storage/base/SingleStorage.java),
and combine them with `CombinedStorage`.

## Fluid transfer
A `Storage<FluidVariant>` is any object that can store fluids. It is just a `Storage<T>`, where `T` is
[`FluidVariant`](src/main/java/net/fabricmc/fabric/api/transfer/v1/fluid/FluidVariant.java), the immutable combination of a `Fluid` and additional NBT data.
Instances can be accessed through the API lookups defined in [`FluidStorage`](src/main/java/net/fabricmc/fabric/api/transfer/v1/fluid/FluidStorage.java).

The unit for fluid transfer is 1/81000ths of a bucket, also known as _droplets_.
[`FluidConstants`](src/main/java/net/fabricmc/fabric/api/transfer/v1/fluid/FluidConstants.java) contains a few helpful constants
to work with droplets.

Client-side [Fluid variant rendering](src/main/java/net/fabricmc/fabric/api/transfer/v1/client/fluid/FluidVariantRendering.java) will use regular fluid rendering by default,
ignoring the additional NBT data.
`Fluid`s that wish to render differently depending on the stored NBT data can register a
[`FluidVariantRenderHandler`](src/main/java/net/fabricmc/fabric/api/transfer/v1/client/fluid/FluidVariantRenderHandler.java).

## Item transfer
A `Storage<ItemVariant>` is any object that can store items.
Instances can be accessed through the API lookup defined in [`ItemStorage`](src/main/java/net/fabricmc/fabric/api/transfer/v1/item/ItemStorage.java).

The lookup already provides compatibility with vanilla inventories, however it may sometimes be interesting to use
[`InventoryStorage`](src/main/java/net/fabricmc/fabric/api/transfer/v1/item/InventoryStorage.java) or
[`PlayerInventoryStorage`](src/main/java/net/fabricmc/fabric/api/transfer/v1/item/PlayerInventoryStorage.java) when interaction with
`Inventory`-based APIs is required.

## `ContainerItemContext`
[`ContainerItemContext`](src/main/java/net/fabricmc/fabric/api/transfer/v1/context/ContainerItemContext.java) is a context designed for `ItemApiLookup` queries
that allows the returned APIs to interact with the containing inventory.
Notably, it is used by the `FluidStorage.ITEM` lookup for fluid-containing items.
