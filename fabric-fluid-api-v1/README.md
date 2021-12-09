# Fabric Fluid Api (V1)

This api is focused on implementing an easy way to customize some basic fluid behaviours.  
It provides also a default implementation for many common behaviours.  
There are also some utilities (aka static methods) useful for doing smart work with fluids.

Here you can see a complete documentation of all the contents of this api:

## FabricFluidBlock

The [`FabricFluidBlock`][fabricfluidblock] class extends the `FluidBlock`
class, but with a public constructor, so is possible to create an instance
of `FabricFluidBlock` directly, instead of extending `FluidBlock` everytime.

There is no changes between [`FabricFluidBlock`][fabricfluidblock]
and `FluidBlock`.

## FabricFlowableFluid

The [`FabricFlowableFluid`][fabricflowablefluid] class extends the
`FlowableFluid` class and implements some common fluid behaviour, to simplify
the fluid creation process, write less code, and avoid bugs.  
It's still an abstract class, because some methods are logically specific for every fluid.  
Remember that is still possible to override the already implemented methods
to change their behaviour.

The implemented methods are:

* `void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state)`  
  Drops the stacks of the broken blocks basing on their loot tables.

* `Optional<SoundEvent> getBucketFillSound()`  
  Returns the `SoundEvents.ITEM_BUCKET_FILL` sound.

* `int getFogColor(Entity entity, float tickDelta, ClientWorld world)`  
  Returns -1 (no color).

* `float getFogEnd(Entity entity, BackgroundRenderer.FogType fogType,
  float viewDistance, boolean thickFog)`  
  Returns the current view distance.

* `float getFogStart(Entity entity, BackgroundRenderer.FogType fogType,
  float viewDistance, boolean thickFog)`  
  Returns 0.

* `int getLevel(FluidState state)`  
  Returns the current fluid level for the flowing fluid state,
  and the max level for still fluid state.

* `boolean matchesType(Fluid fluid)`  
  Returns `true` if the given fluid is an instance of the current flowable
  or still fluid.

## FluidMatching and FluidInteractions

The [`FluidMatching`][fluidmatching] and [`FluidInteractions`][fluidinteractions] classes contains some utilities
about checking fluids properties, fluid matching, and interactions with fluids.  
All of these are static methods.

[`FluidMatching`][fluidmatching] contains:

* `boolean areEqual(FluidState fluidState1, FluidState fluidState2)`  
  Checks if two FluidState are equal.

* `boolean areEqual(Fluid fluid1, Fluid fluid2)`  
  Checks if two Fluid are equal.

* `boolean isIn(FluidState fluidState, Tag<Fluid> tag)`  
  Checks if a FluidState is in a specified tag.

* `boolean isIn(Fluid fluid, Tag<Fluid> tag)`  
  Checks if a Fluid is in a specified tag.

* `boolean isFabricFluid(FluidState state)`  
  Check if a FluidState is a fabric custom fluid.

* `boolean isFabricFluid(Fluid fluid)`  
  Check if a Fluid is a fabric custom fluid.

[`FluidInteractions`][fluidinteractions] contains:

* `FluidState getSubmergedFluid(Entity entity)`  
  Get the fluid in which the entity is submerged.

* `boolean isTouching(Entity entity, Tag<Fluid> tag)`  
  Checks if the specified entity is touching a fluid with the specified tag.

* `boolean isTouching(Box box, World world, Tag<Fluid> tag)`  
  Checks if the specified box is touching a fluid with the specified tag.




[fabricfluidblock]: src/main/java/net/fabricmc/fabric/api/fluid/v1/FabricFluidBlock.java
[fabricflowablefluid]: src/main/java/net/fabricmc/fabric/api/fluid/v1/FabricFlowableFluid.java
[fluidmatching]: src/main/java/net/fabricmc/fabric/api/fluid/v1/util/FluidMatching.java
[FluidInteractions]: src/main/java/net/fabricmc/fabric/api/fluid/v1/util/FluidInteractions.java
