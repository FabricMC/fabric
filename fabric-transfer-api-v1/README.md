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
Its content can be read, and resources can be inserted into it or extracted from it.
[`Movement`](src/main/java/net/fabricmc/fabric/api/transfer/v1/storage/Movement.java) can be used to move resources between two `Storage`s.
The [`storage/base`](src/main/java/net/fabricmc/fabric/api/transfer/v1/storage/base) package provides a few helpers accelerate
implementation of `Storage<T>`.

## Fluid transfer
A `Storage<FluidKey>` is any object that can store fluids. It is just a `Storage<T>`, where `T` is
[`FluidKey`](src/main/java/net/fabricmc/fabric/api/transfer/v1/fluid/FluidKey.java), the immutable combination of a `Fluid` and additional NBT data.
Instances can be accessed through the API lookups defined in [`FluidTransfer`](src/main/java/net/fabricmc/fabric/api/transfer/v1/fluid/FluidTransfer.java).

Implementors of fluid inventories with a fixed number of "slots" or "tanks" can use
[`SingleFluidStorage`](src/main/java/net/fabricmc/fabric/api/transfer/v1/fluid/base/SingleFluidStorage.java),
and combine them with `CombinedStorage`.
Usage of [`FluidPreconditions`](src/main/java/net/fabricmc/fabric/api/transfer/v1/fluid/FluidPreconditions.java) is recommended to detect
wrong usage of `Storage` and `StorageView` methods.

The amount for fluid transfer is droplets, that is 1/81000ths of a bucket.
[`FluidConstants`](src/main/java/net/fabricmc/fabric/api/transfer/v1/fluid/FluidConstants.java) contains a few helpful constants
to work with droplets.

Client-side [Fluid key rendering](src/main/java/net/fabricmc/fabric/api/transfer/v1/client/fluid/FluidKeyRendering.java) will use regular fluid rendering by default,
ignoring the additional NBT data.
`Fluid`s that wish to render differently depending on the stored NBT data can register a
[`FluidKeyRenderHandler`](src/main/java/net/fabricmc/fabric/api/transfer/v1/client/fluid/FluidKeyRenderHandler.java).
