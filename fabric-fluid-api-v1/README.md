# Fabric Fluid Api (V1)

This api is focused on improving everything about the fluid creation,
compared with vanilla one.

Here you can see a complete "ready to go" documentation
of all the contents of this api:

## Fluid rendering

You can easyly render your fluid with his textures by using
the static `render()` methods of the [`FluidRenderer`][fluidrenderer_java]
class by specifying the texture id.

There are two version of the render() method:

* `render(Fluid still, Fluid flowing, Identifier textureID)`  
  Parameters:
  - `still (Fluid)`: The still variant of the fluid.
  - `flowing (Fluid)`: The flowing variant of the fluid.
  - `textureID (Identifier)`: The identifier of the texture to use.

* `render(Fluid still, Fluid flowing, Identifier textureID, int color)`  
  This is the same as the method above, but requires also:
  - `color (int)`: The color used to recolorize the fluid texture.

This method must be called on client side.

Based on the given id the textures are searched into these paths:
* `block/[TEXTURE_ID.PATH]_still` for the still variant.
* `block/[TEXTURE_ID.PATH]_flow` for the flowing variant.

For example if your texture Identifier is:

`new Identifier("tutorial", "red_fluid")`

The two textures, in the textures' folder, are: `block/red_fluid_still.png`
and `block/red_fluid_flow.png`

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
contains five methods allowing to specify the fluid sounds.

* `SoundParameters getEnterSound(World world, Entity entity)`  
  Allows to specify the sound to play when the player enters the fluid.

* `SoundParameters getExitSound(World world, Entity entity)`  
  Allows to specify the sound to play when the player exit from the fluid.

* `SoundParameters getSplashSound(World world, Entity entity)`  
  Allows to specify the splash sound of the fluid.

* `Optional<SoundEvent> getSwimSound(World world, Entity entity)`  
  Allows to specify the swim sound of the fluid.

* `SoundParameters getSubmergedAmbientSound(World world, Entity entity)`  
  Allows to specify the ambient sound to play when the player is submerged by the fluid.

**NOTE:** `SoundParameters` is an object that takes a SoundEvent
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

Costomizations applicable by setting [tags][fabric_fluid_tags] on the fluid:

### Fire extinguisher

Fluids with the tag `fabric:fire_extinguisher`
can extinguish fire on entities on fire.

### Fire lighter

Fluids with the tag `fabric:fire_lighter`
can light fire on entities and burnable blocks around it.

### Wet

Fluids with the tag `fabric:wet`
can to wet entities, and damage them, if are damageable by wet fluids,
like endermans.

**NOTE:** If a fluid can to wet, it cannot light fire on entities.

### Prevent fall damage

Fluids with the tag `fabric:prevent_fall_damage`
can prevent fall damage.

### Respirable

Fluids with the tag `fabric:respirable`
are respirable by entities, and will not cause drowning.

### Swimmable

Fluids with the tag `fabric:swimmable`
can be swimmable by entities.

**NOTE:** In a non-swimmable fluid is only possible to jump from the ground,
but not to swim, like in quicksands.

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

## Fluid events

### Splash event

The [`FabricFlowableFluid`][fabricflowablefluid_java] class
contains method that is executed
when the player splashes on the fluid (like jumping).
This method is useful to spawn particles and play splash sounds.

`void onSplash(World world, Entity entity)`

### Submerged event

The [`FabricFlowableFluid`][fabricflowablefluid_java] class
contains a method that is executed every tick,
when the player is submerged by the fluid.
This method is useful to handle drowning.

`void onSubmerged(World world, Entity entity)`

### Touching event

The [`FabricFlowableFluid`][fabricflowablefluid_java] class
contains a method that is executed every tick,
when the player is touching the fluid.
This method is useful to handle setting entities on fire.

`void onTouching(World world, Entity entity)`

## FabricFlowableFluid

The [`FabricFlowableFluid`][fabricflowablefluid_java] class extends the
`FlowableFluid` class and implements some common fluid behaviour, to simplofy
the fluid creation process.
It's still an abstract class, so you have to implement some specific methods.
Remember that is still possible to override the implemented methods
to change their behaviour.

The implemented methods are:

* `Optional<SoundEvent> getBucketFillSound()`  
  Returns the `SoundEvents.ITEM_BUCKET_FILL` sound.

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

* `int getMaxLevel(FluidState state)`  
  Returns the max level that the fluid can have (by default is 8).

* `SoundParameters getEnterSound(World world, Entity entity)`  
  Returns the `SoundEvents.AMBIENT_UNDERWATER_ENTER` sound with volume 1 and pitch 1.

* `SoundParameters getExitSound(World world, Entity entity)`  
  Returns the `SoundEvents.AMBIENT_UNDERWATER_EXIT` sound with volume 1 and pitch 1.

* `SoundParameters getSplashSound(World world, Entity entity)`  
  Returns the `SoundEvents.ENTITY_GENERIC_SPLASH` sound with volume 0.2 and pitch 1.

* `Optional<SoundEvent> getSwimSound(World world, Entity entity)`  
  Returns the `SoundEvents.ENTITY_GENERIC_SWIM` sound.

* `SoundParameters getSubmergedAmbientSound(World world, Entity entity)`  
  Returns the `SoundEvents.AMBIENT_UNDERWATER_LOOP` sound with volume 1 and pitch 1.

* `double getViscosity(World world, Entity entity)`  
  Returns 0.014 (the default water viscosity).

* `boolean canBeReplacedWith(FluidState state, BlockView world,
  BlockPos pos, Fluid fluid, Direction direction)`  
  Returns `false` (the fluid will not be replaceable by other fluids).

* `int getLevel(FluidState state)`  
  Returns the current fluid level for the flowing fluid state,
  and the max level for still fluid state.

* `boolean matchesType(Fluid fluid)`  
  Returns `true` if the given fluid is an instance of the current flowable
  or still fluid.

* `boolean hasRandomTicks()`  
  Returns `true` if the fluid can light fire.

* `void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state)`  
  Drops the stacks of the broken blocks basing on their loot tables.

* `void onRandomTick(World world, BlockPos pos, FluidState state, Random random)`  
  Implements setting burnable blocks on fire.

* `void onSplash(World world, Entity entity)`  
  Does nothing.

* `void onSubmerged(World world, Entity entity)`  
  Implements drowning for every living entity.

* `void onTouching(World world, Entity entity)`  
  Implements setting entities on fire.

## How to add the customization tags

To add the [customizations tags][fabric_fluid_tags], create a **json** file
for each tag you want to use inside `/resources/data/fabric/tags/fluids`.  
The file names are respectively:

* **Fire extinguisher:** `fire_extinguisher.json`
* **Fire lighter:** `fire_lighter.json`
* **Navigable:** `navigable.json`
* **Prevent fall damage:** `prevent_fall_damage.json`
* **Respirable:** `respirable.json`
* **Swimmable:** `swimmable.json`
* **Wet:** `wet.json`

Then add your fluid ids, in both still and flowing variant.

```json
{
  "replace": false,
  "values":
  [
    "modid:still_fluid_id",
    "modid:flowing_fluid_id"
  ]
}
```



[fluidrenderer_java]: src/main/java/net/fabricmc/fabric/api/fluid/v1/render/FluidRenderer.java
[fabricfluidblock_java]: src/main/java/net/fabricmc/fabric/api/fluid/v1/FabricFluidBlock.java
[fabricflowablefluid_java]: src/main/java/net/fabricmc/fabric/api/fluid/v1/FabricFlowableFluid.java
[fabric_fluid_tags]: src/main/java/net/fabricmc/fabric/api/fluid/v1/tag/FabricFluidTags.java
