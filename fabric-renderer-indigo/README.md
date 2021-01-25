# Fabric Renderer Indigo

Indigo is the default implementation of the Fabric Renderer API.
Indigo is meant to deviate from vanilla appearance as little as possible.

## Custom values

### `fabric-renderer-api-v1:contains_renderer`

If a mod contains this custom value, the default indigo renderer will be disabled.
This is intended to be specified in mods which implement the rendering api.

**Type:** Any

**Presence:**
This custom value should be used by mods which implement the rendering api.

### `fabric-renderer-indigo:force_compatibility`

If a mod contains this value AND no mod has set `fabric-renderer-api-v1:contains_renderer`, the Indigo renderer implementation will use a compatibility mode.

This will disable vanilla block tesselation and ensures vertex format compatibility.

**Type:** Any

**Presence:**
This custom value should be used by mods which require the vanilla vertex format for compatibility.
