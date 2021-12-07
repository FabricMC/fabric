# Fabric Fluid Api (V1)

This api is focused on implementing an easy way to create custom fluids,
and customize a lot of properties, such as sounds, fog, swim, hot damage...

It also allows to simplify a lot of things and write less code
avoiding unnecessarily repeating many common things,
starting with the extension of the `FluidBlock` class to get a public constructor.

Here you can see a complete "ready to go" documentation
of all the contents of this api:

## FabricFluidBlock

The [`FabricFluidBlock`][fabricfluidblock_java] class extends the `FluidBlock`
class, but with a public constructor, so is possible to create an instance
of `FabricFluidBlock` directly, instead of extending `FluidBlock` everytime.

There is no changes between [`FabricFluidBlock`][fabricfluidblock_java]
and `FluidBlock`.

## Fluid properties

### Fog

The [`FabricFlowableFluid`][fabricflowablefluid_java] class
contains three methods allowing to specify some fluid fog parameters.

* `int getFogColor(Entity entity, float tickDelta, ClientWorld world)`  
  Allows to specify the fog color.

* `float getFogEnd(Entity entity, BackgroundRenderer.FogType fogType,
  float viewDistance, boolean thickFog)`  
  Allows to specify the fog ending distance.

* `float getFogStart(Entity entity, BackgroundRenderer.FogType fogType,
  float viewDistance, boolean thickFog)`  
  Allows to specify the fog starting distance.

### Viscosity (pushing strength)

The [`FabricFlowableFluid`][fabricflowablefluid_java] class
contains a method allowing to specify the viscosity of the fluid.

`double getViscosity(World world, Entity entity)`

### Sounds

The [`FabricFlowableFluid`][fabricflowablefluid_java] class
contains seven methods allowing to specify the fluid sounds.

* `SoundParameters getEnterSound(World world, Entity entity)`  
  Allows to specify the sound to play when the player enters the fluid.

* `SoundParameters getExitSound(World world, Entity entity)`  
  Allows to specify the sound to play when the player exit from the fluid.

* `Optional<SoundEvent> getGenericSplashSound()`  
  Allows to specify the generic splash sound of the fluid.  
  **NOTE 1:** This is mainly used by boats when they are in bubble columns.  
  **NOTE 2:** If null, will be used the default pre-defined sound.

* `Optional<SoundEvent> getPaddleSound()`  
  Allows to specify the sound to play when a boat navigates on the fluid.

* `SoundParameters getSplashSound(World world, Entity entity)`  
  Allows to specify the splash sound of the fluid.

* `Optional<SoundEvent> getSwimSound()`  
  Allows to specify the swim sound of the fluid.

* `SoundParameters getSubmergedAmbientSound(World world, Entity entity)`  
  Allows to specify the ambient sound to play when the player is submerged by the fluid.

**NOTE 3:** `SoundParameters` is an object that takes a SoundEvent
and two floats for volume and pitch.

### Hot damage

The [`FabricFlowableFluid`][fabricflowablefluid_java] class
contains a method allowing to specify the damage that the fluid
can do, every tick, when touched (like lava).

`float getHotDamage(World world)`

### Fire duration

The [`FabricFlowableFluid`][fabricflowablefluid_java] class
contains a method allowing to specify the duration of the fire
applied to entities, if the fluid can light fire and cannot to wet.

**NOTE:** This property is ignored if the fluid cannot light fire
or can to wet.

`int getEntityOnFireDuration(World world)`

## Fluid properties by tags

Customizations applicable by setting [tags][fabric_fluid_tags] on the fluid:

### Breathable

Fluids with the tag `fabric:breathable`
are breathable by entities, and will not cause them drowning.

### Breathable by aquatic

Fluids with the tag `fabric:breathable_by_aquatic`
are breathable by aquatic entities, and will not cause them drowning.

### Can extinguish fire

Fluids with the tag `fabric:can_extinguish_fire`
can extinguish fire on entities on fire.

### Can light fire

Fluids with the tag `fabric:can_light_fire`
can light fire on entities and burnable blocks around it.

### Ignore depth strider

Fluids with the tag `fabric:ignore_depth_strider`
will ignore depth strider.

### Navigable

Fluids with the tag `fabric:navigable`
can be navigable with boats.

**NOTE 1:** In a non-navigable fluid the boat will sink.

**NOTE 2:** To render boats correctly, the fluids must be rendered
as translucent with `RenderLayer.getTranslucent()`.

Add in your client-side entrypoint:

```java
BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
        FLUID, FLOWING_FLUID);
```

### Prevent fall damage

Fluids with the tag `fabric:prevent_fall_damage`
can prevent fall damage.

### Swimmable

Fluids with the tag `fabric:swimmable`
can be swimmable by entities.

**NOTE:** In a non-swimmable fluid is only possible to jump from the ground,
but not to swim, like in quicksands.

### Wet

Fluids with the tag `fabric:wet` can to wet entities,
and damage them, if are damageable by wet fluids, like endermans.

**NOTE:** If a fluid can to wet, it cannot light fire on entities.

## Fluid events

### Splash event

The [`FabricFlowableFluid`][fabricflowablefluid_java] class
contains method that is executed
when the player splashes on the fluid (like jumping).

`void onSplash(World world, Entity entity)`

### Submerged event

The [`FabricFlowableFluid`][fabricflowablefluid_java] class
contains a method that is executed every tick,
when the player is submerged by the fluid.

`void onSubmerged(World world, Entity entity)`

### Touching event

The [`FabricFlowableFluid`][fabricflowablefluid_java] class
contains a method that is executed every tick,
when the player is touching the fluid.

`void onTouching(World world, Entity entity)`

## FabricFlowableFluid

The [`FabricFlowableFluid`][fabricflowablefluid_java] class extends the
`FlowableFluid` class and implements some common fluid behaviour, to simplify
the fluid creation process, write less code, and avoid bugs.  
It's still an abstract class, because some methods are
logically specific for every fluid.  
Remember that is still possible to override the already implemented methods
to change their behaviour.

The implemented methods are:

* `Optional<SoundEvent> getBucketFillSound()`  
  Returns the `SoundEvents.ITEM_BUCKET_FILL` sound.

* `SoundParameters getEnterSound(World world, Entity entity)`  
  Returns the `SoundEvents.AMBIENT_UNDERWATER_ENTER` sound with volume 1 and pitch 1.

* `SoundParameters getExitSound(World world, Entity entity)`  
  Returns the `SoundEvents.AMBIENT_UNDERWATER_EXIT` sound with volume 1 and pitch 1.

* `Optional<SoundEvent> getGenericSplashSound()`  
  Returns no sound. (Will be used the default pre-defined sound)

* `Optional<SoundEvent> getPaddleSound()`  
  Returns the `SoundEvents.ENTITY_BOAT_PADDLE_WATER` sound.

* `SoundParameters getSplashSound(World world, Entity entity)`  
  Returns the `SoundEvents.ENTITY_GENERIC_SPLASH` sound with volume 0.1 and pitch 1.

* `Optional<SoundEvent> getSwimSound()`  
  Returns the `SoundEvents.ENTITY_GENERIC_SWIM` sound.

* `SoundParameters getSubmergedAmbientSound(World world, Entity entity)`  
  Returns the `SoundEvents.AMBIENT_UNDERWATER_LOOP` sound with volume 1 and pitch 1.

* `boolean canBeReplacedWith(FluidState state, BlockView world,
  BlockPos pos, Fluid fluid, Direction direction)`  
  Returns `false` (the fluid will not be replaceable by other fluids).

* `int getEntityOnFireDuration(World world)`  
  Returns 15 seconds (the default lava fire duration).

* `int getFogColor(Entity entity, float tickDelta, ClientWorld world)`  
  Returns -1 (no color).

* `float getFogEnd(Entity entity, BackgroundRenderer.FogType fogType,
  float viewDistance, boolean thickFog)`  
  Returns the current view distance.

* `float getFogStart(Entity entity, BackgroundRenderer.FogType fogType,
  float viewDistance, boolean thickFog)`  
  Returns 0.

* `float getHotDamage(World world)`  
  Returns 0.

* `int getLevel(FluidState state)`  
  Returns the current fluid level for the flowing fluid state,
  and the max level for still fluid state.

* `int getMaxLevel(FluidState state)`  
  Returns the max level that the fluid can have
  (by default is 8, that is the maximum level possible).

* `double getViscosity(World world, Entity entity)`  
  Returns 0.014 (the default water viscosity).

* `boolean hasRandomTicks()`  
  Returns `true` if the fluid can light fire.

* `boolean matchesType(Fluid fluid)`  
  Returns `true` if the given fluid is an instance of the current flowable
  or still fluid.

* `void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state)`  
  Drops the stacks of the broken blocks basing on their loot tables.

* `void onRandomTick(World world, BlockPos pos, FluidState state, Random random)`  
  Implements setting burnable blocks on fire.

* `void onSplash(World world, Entity entity)`  
  Does nothing.

* `void onSubmerged(World world, Entity entity)`  
  Does nothing.

* `void onTouching(World world, Entity entity)`  
  Implements damaging entities with fire.

## How to add the customization tags

To add the [customizations tags][fabric_fluid_tags], create a **json** file
for each tag you want to use inside `/resources/data/fabric/tags/fluids`.  
The file names are respectively:

* **Breathable:** `breathable.json`
* **Breathable by aquatic:** `breathable_by_aquatic.json`
* **Can extinguish fire:** `can_extinguish_fire.json`
* **Can light fire:** `can_light_fire.json`
* **Ignore depth strider:** `ignore_depth_strider.json`
* **Navigable:** `navigable.json`
* **Prevent fall damage:** `prevent_fall_damage.json`
* **Swimmable:** `swimmable.json`
* **Wet:** `wet.json`

Then add your fluid ids, in both still and flowing variant.

```json
{
  "replace": false,
  "values":
  [
    "mod_id:still_fluid_id",
    "mod_id:flowing_fluid_id"
  ]
}
```

## FluidUtils

The [`FluidUtils`][fluid_utils] class contains some utilities about handling fluids
and interactions with fluids.



[fabricfluidblock_java]: src/main/java/net/fabricmc/fabric/api/fluid/v1/FabricFluidBlock.java
[fabricflowablefluid_java]: src/main/java/net/fabricmc/fabric/api/fluid/v1/FabricFlowableFluid.java
[fabric_fluid_tags]: src/main/java/net/fabricmc/fabric/api/fluid/v1/tag/FabricFluidTags.java
[fluid_utils]: src/main/java/net/fabricmc/fabric/api/fluid/v1/util/FluidUtils.java
