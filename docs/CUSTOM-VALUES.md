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
These custom values are documented in each module which consumes these types of custom values.
