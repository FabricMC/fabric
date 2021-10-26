# Fabric Fluid Api (V1)

This api is focused on improving everything about the fluid creation,
compared with vanilla one.

## Content

- [x] Ready-to-use basic fluid rendering.
- [x] Ready-to-use basic abstract FlowableFluid class.
- [x] Ready-to-use basic FluidBlock class.
- [x] Custom fog.
- [x] Pushing strength.
- [x] Splash event.
- [x] Splash event: splash sounds and particles.
- [x] Submerged event.
- [x] Submerged event: entity drowning.
- [x] Handling fire and fall.
- [ ] Entity swimming.
- [ ] Boats over the fluid.

## Ready-to-use basic fluid rendering

The [`FluidRenderer`][fluidrenderer_java]
class implements a `render()` method that handles the fluid rendering
with the given texture id.

There are two version of the render() method:

* `render(Fluid, Fluid, Identifier)`  
  Which requires 3 parameters:
  - `still (Fluid)`: The still variant of the fluid.
  - `flowing (Fluid)`: The flowing variant of the fluid.
  - `textureID (Identifier)`: The identifier of the texture to use.

* `render(Fluid, Fluid, Identifier, int)`  
  Which is the same as the method above, but requires also:
  - `color (int)`: The color used to recolorize the fluid texture.

This method must be called on client side.

Based on the given id the textures are searched into these paths:
* `block/[TEXTURE_ID.PATH]_still` for the still variant.
* `block/[TEXTURE_ID.PATH]_flow` for the flowing variant.

For example if your texture Identifier is:

`new Identifier("tutorial", "red_fluid")`

The two textures, in the textures' folder, are: `block/red_fluid_still.png`
and `block/red_fluid_flow.png`

## Ready-to-use basic abstract FlowableFluid class

The [`FabricFlowableFluid`][fabricflowablefluid_java] class extends the
`FlowableFluid` class and implements some common methods, to avoid
reimplementing them.
It's still an abstract class, so you have to implement some specific methods.
Remember that is still possible to override the implemented methods
to change their behavior.

The already implemented methods are:

* `void beforeBreakingBlock(WorldAccess, BlockPos, BlockState)`  
  Drops the stacks of the broken blocks basing on their loot tables.

* `boolean canBeReplacedWith(FluidState, BlockView, BlockPos, Fluid, Direction)`  
  Returns `false` (the fluid will not be replaceable by other fluids).

* `Optional<SoundEvent> getBucketFillSound()`  
  Returns the basic bucket fill sound: `SoundEvents.ITEM_BUCKET_FILL`.

* `int getLevel(FluidState)`  
  Returns the current fluid level for the flowing fluid state,
  and the max level for still fluid state.

* `int getMaxLevel(FluidState)`  
  This method is not present in `FlowableFluid`, however it returns
  the max level that the fluid can have (by default is 8).

* `boolean matchesType(Fluid)`  
  It returns `true` if the given fluid is an instance of the current flowable
  or still fluid.

There is also the [`ExtendedFabricFlowableFluid`][extendedfabricflowablefluid_java]
that implements some methods from [`ExtendedFlowableFluid`][extendedflowablefluid_java].

It implements:

* `boolean canExtinguishFire()`  
  Returns `true` (the fluid can extinguish fire).

* `boolean canPreventFallDamage()`  
  Returns `true` (the fluid can prevent fall damage).

* `void onSplash(World, Vec3d, Entity)`  
  Does nothing.

* `void onSubmerged(World, Entity)`  
  Implements drowning for every living entity.

## Ready-to-use basic FluidBlock class

The [`FabricFluidBlock`][fabricfluidblock_java] class extends the `FluidBlock`
class, but with a public constructor, so is possible to create an instance
of `FabricFluidBlock` directly, instead of extending `FluidBlock` everytime.

There is no changes between [`FabricFluidBlock`][fabricfluidblock_java]
and `FluidBlock`.

## Custom fog

By implementing the [`ExtendedFlowableFluid`][extendedflowablefluid_java]
interface on your fluid class, you can implement three methods allowing to
specify the fog parameters.

* `int getFogColor(Entity)`  
  Allows to specify the fog color.

* `float getFogEnd(Entity)`  
  Allows to specify the fog ending distance.

* `float getFogStart(Entity)`  
  Allows to specify the fog starting distance.

## Pushing strength

By implementing the [`ExtendedFlowableFluid`][extendedflowablefluid_java]
interface on your fluid class, you can implement a method allowing to
specify the pushing strength of the fluid.

`double getViscosity(World, Vec3d, Entity)`

## Splash event

By implementing the [`ExtendedFlowableFluid`][extendedflowablefluid_java]
interface on your fluid class, you can implement a method that is executed
when the player splashes on the fluid (like jumping).
This method is useful to spawn particles and play splash sounds.

`void onSplash(World, Vec3d, Entity)`

## Submerged event

By implementing the [`ExtendedFlowableFluid`][extendedflowablefluid_java]
interface on your fluid class, you can implement a method that is executed
every tick, when the player is submerged by the fluid.
This method is useful to handle drowning.

`void onSubmerged(World, Entity)`

## Handling fire and fall

By implementing the [`ExtendedFlowableFluid`][extendedflowablefluid_java]
interface on your fluid class, you can implement two methods allowing to
specify if the fluid can extinguish fire and prevent fall damage.

* `boolean canExtinguishFire()`  
  Allows to specify if the fluid can extinguish fire.

* `boolean canPreventFallDamage()`  
  Allows to specify if the fluid can prevent fall damage.

## The `fabric:fabric_fluid` tag

To implement all the features of this api, your fluid
must be added in the [`fabric:fabric_fluid`][fabric_fluid_tag] tag, so add
a file named `fabric_fluid.json` inside `/resources/data/fabric/tags/fluids`
and add your fluid here, in both still and flowing variant.

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
[fabricflowablefluid_java]: src/main/java/net/fabricmc/fabric/api/fluid/v1/FabricFlowableFluid.java
[extendedfabricflowablefluid_java]: src/main/java/net/fabricmc/fabric/api/fluid/v1/ExtendedFabricFlowableFluid.java
[fabricfluidblock_java]: src/main/java/net/fabricmc/fabric/api/fluid/v1/FabricFluidBlock.java
[extendedflowablefluid_java]: src/main/java/net/fabricmc/fabric/api/fluid/v1/ExtendedFlowableFluid.java
[fabric_fluid_tag]: src/main/java/net/fabricmc/fabric/api/fluid/v1/tag/FabricFluidTags.java
