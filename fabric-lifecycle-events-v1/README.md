# Fabric Lifecycle Events (V1)

## Organization

Events in this module belong to a single class which relates to the category of events.
The implementation of event callbacks are sub classes.

For example, `ServerLifecycleEvents.SERVER_STARTING` is in `ServerLifecycleEvents` and the implementation of the event's callback would implement `ServerLifecycleEvents.ServerStarting`.

# Server Events

<details><summary>Events related to objects on a logical server.</summary>

## `ServerLifecycleEvents`

<details><summary>Contains lifecycle events related to a Minecraft Server.</summary>

#### `ServerLifecycleEvents.SERVER_STARTING`

- Called when a Minecraft server is starting. This is called before any worlds are loaded, no players are connected yet and the `PlayerManager` is null.
- Mods which rely on tracking the current server instance may use this event to capture the server instance.

#### `ServerLifecycleEvents.SERVER_STARTED`

- Called when a Minecraft server has finshed starting and is about to tick for the first time. When this event is called, all worlds have been loaded and players are ready to be accepted.

#### `ServerLifecycleEvents.SERVER_STOPPING`

- Called when a Minecraft server has started shutting down.
- This occurs before the server's network channel is closed and before any connected players are disconnected.

#### `ServerLifecycleEvents.SERVER_STOPPED`

- Called when a Minecraft server has stopped.
All worlds have been closed and all (block) entities and players have been unloaded.
- If a mod stores the server instance anywhere, this event should be used for reference cleanup.
- Depending on the implementation of the server the following may occur after this event is called:
    - On an integrated server (`EnvType.CLIENT`), the client will continue running.
    - On a dedicated server (`EnvType.SERVER`), this will be the last event called. The JVM will terminate after this event.

#### `ServerLifecycleEvents.START_DATA_PACK_RELOAD`
- Called before a Minecraft server reloads data packs.
- In vanilla, this is typically called after `/reload` command is executed.

#### `ServerLifecycleEvents.END_DATA_PACK_RELOAD`
- Called after a Minecraft server has reloaded data packs.
- This event does not garuntee the data pack reload was successful.
- Per the `EndDataPackReload` interface, the last `boolean` parameter specifies whether the data pack reload was successful.

```java
@FunctionalInterface
public interface EndDataPackReload {
    void endDataPackReload(MinecraftServer server, ServerResourceManager serverResourceManager, boolean success);
}
```
- If the data pack reload is successful, data packs will be set. If the reload failed, then the currently loaded data packs will be kept.
</details>

## `ServerTickEvents`

<details><summary>Contains events related to the ticking of a Minecraft server.</summary>

#### `ServerTickEvents.START_SERVER_TICK`
- Called at the start of the server tick.
- This event can be used by mods to pre-process before the server tick occurs.

#### `ServerTickEvents.END_SERVER_TICK`
- Called at the end of the server tick.

#### `ServerTickEvents.START_WORLD_TICK`
- Called at the start of a ServerWorld's tick.

#### `ServerTickEvents.END_WORLD_TICK`
- Called at the end of a ServerWorld's tick.
- End of world tick may be used to start async computations for the next tick.
</details>

## `ServerWorldEvents`

<details><summary>Events related to the lifecycle of server worlds.</summary>

#### `ServerWorldEvents.LOAD`
- Called when a world is loaded by a Minecraft server.
- This event may be called at any time, but is typically called between `ServerLifecycleEvents.SERVER_STARTING` and `ServerLifecycleEvents.SERVER_STARTED`.
- Mods which implement dynamically loaded dimensions may call this event to notify other mods of a new server world being loaded.

#### `ServerWorldEvents.UNLOAD`
- Called before a world is unloaded by a Minecraft server.
- This event may be called at any time but is typically called after a server has started shutting down (`ServerLifecycleEvents.SERVER_STOPPING`).
- Mods which implement dynamically loaded dimensions may call this event to notify other mods of a server world being unloaded for reference cleanup.
</details>

## `ServerChunkEvents`

<details><summary>Events related to the lifecycle of chunks in a server world.</summary>

#### `ServerChunkEvents.CHUNK_LOAD`
- Called when an chunk is loaded into a ServerWorld.
- The chunk may be modified when this event is called.

#### `ServerChunkEvents.CHUNK_UNLOAD`
- Called when an chunk is unloaded from a ServerWorld.
- The chunk may be modified when this event is called.
</details>

## `ServerEntityEvents`

<details><summary>Events related to the lifecycle of entities in a server world.</summary>

#### `ServerEntityEvents.ENTITY_LOAD`
- Called when an Entity is loaded into a ServerWorld.
- The entity is present in the server world when this event is called.
- **The unload event has not been implemented yet.**
</details>

## `ServerBlockEntityEvents`

<details><summary>Events related to the lifecycle of block entities in a server world.</summary>

#### `ServerBlockEntityEvents.BLOCK_ENTITY_LOAD`
- Called when an BlockEntity is loaded into a ServerWorld.
- The block entity is present in the server world when this event is called.

#### `ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD`
- Called when an BlockEntity is about to be unloaded from a ServerWorld.
- The block entity is present in the server world when this event is called.
</details>

</details>

# Client Events

<details><summary>Events related to objects on a logical client.</summary>

**Note: These events are only available on a client. Try to access these events on a dedicated server will cause the game to crash.**

## `ClientLifecycleEvents`

<details><summary>Events related to the lifecycle of a Minecraft client</summary>

#### `ClientLifecycleEvents.CLIENT_STARTED`
- Called when Minecraft has started and it's client about to tick for the first time.
- This occurs while the splash screen is displayed.

#### `ClientLifecycleEvents.CLIENT_STOPPING`
- Called when Minecraft's client begins to stop.
- This is caused by quitting while in game, or closing the game window.
- This is called before the integrated server is stopped.
</details>

## `ClientTickEvents`

<details><summary>Events related to ticking of a Minecraft client.</summary>

#### `ClientTickEvents.START_CLIENT_TICK`
- Called at the start of the client tick.

#### `ClientTickEvents.END_CLIENT_TICK`
- Called at the end of the client tick.

#### `ClientTickEvents.START_WORLD_TICK`
- Called at the start of a client world's tick.

#### `ClientTickEvents.END_WORLD_TICK`
- Called at the end of a client world's tick.
- End of world tick may be used to start async computations for the next tick.
</details>

## `ClientChunkEvents`

<details><summary>Events related to the lifecycle of chunks on a Minecraft client</summary>

#### `ClientChunkEvents.CHUNK_LOAD`
- Called when a chunk is loaded into a client world.

#### `ClientChunkEvents.CHUNK_UNLOAD`
- Called when a chunk is about to be unloaded from a client world.
</details>

## `ClientEntityEvents`

<details><summary>Events related to the lifecycle of entities in a client world</summary>

#### `ClientEntityEvents.ENTITY_LOAD`
- Called when an Entity is loaded into a client world.
- The entity is present in the client world when this event is called.

#### `ClientEntityEvents.ENTITY_UNLOAD`
- Called when an Entity is about to be unloaded from a client world.
- The entity is present in the client world when this event is called.
</details>

## `ClientBlockEntityEvents`

<details><summary>Events related to the lifecycle of block entities in a client world</summary>

#### `ClientBlockEntityEvents.BLOCK_ENTITY_LOAD`
- Called when a BlockEntity is loaded into a client world.
- The block entity is present in the client world when this event is called.

#### `ClientBlockEntityEvents.BLOCK_ENTITY_LOAD`
- Called when a BlockEntity is about to be unloaded from a client world.
- The block entity is present in the client world when this event is called.
</details>

</details>
