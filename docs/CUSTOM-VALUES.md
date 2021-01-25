# Custom values

Fabric API modules may have certain custom values set or check for the presence of specific custom values.

The use of custom values ranges from enabling/disabling features to extra metadata about fabric api modules.

# Provided by Fabric API

Custom values provided by Fabric API are for use by other mods.

## `fabric-api:module-lifecycle`

Specifies the lifecycle of a fabric api module.
This may be used by other mods to determine the current lifecycle of a fabric api module, such as if a module is experimental
and may be subject to unannounced changes.

**Type:** Json String

**Presence:**
All Fabric API modules

**Allowed values:**
 - `stable`
 - `experimental`
 - `deprecated`

**Example:**
```json
"custom": {
    "fabric-api:module-lifecycle": "stable"
}
```

# Consumed by Fabric API

Custom values consumed by Fabric API are specified by other mods.
These may be used to enable/disable features in fabric api.

## Fabric Renderer Indigo

### `fabric-renderer-api-v1:contains_renderer`

If a mod contains this custom value, the default indigo renderer will be disabled.
This is intended for mods which implement the rendering api.

**Type:** Any

**Presence:**
A mod which implements the rendering api.

### `fabric-renderer-indigo:force_compatibility`

If a mod contains this value AND no mod has set `fabric-renderer-api-v1:contains_renderer`, the Indigo renderer implementation will use a compatability mode.

This will disable vanilla block tesselation and ensures vertex format compatibility.

**Type:** Any

**Presence:**
A mod which requires vanilla vertex format compatability.
