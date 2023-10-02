Provides utility to layer minecraft gui screens on top of one another.

Examples Usages:

To add a screen on top of another screen:
```java
ScreenLayer.push(new CustomScreen());
```

To remove a screen:
```java
ScreenLayer.pop();
```

To clear all screens:
```java
ScreenLayer.clear();
```

When translating a screen or item on the screen for sizing.
It is important to use `ScreenLayer.getFarPlane()`so it does not break mods using layers.
```java
RenderSystem.clear(GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
Matrix4f matrix4f = new Matrix4f().setOrtho(0.0F, (float) width, (float) height, 0.0F, 100.0F, ScreenLayer.getFarPlane());
RenderSystem.setProjectionMatrix(matrix4f, VertexSorter.BY_Z);
MatrixStack stack = RenderSystem.getModelViewStack();
stack.loadIdentity();
stack.translate(0.0D, 0.0D, 1000.0F - ScreenLayer.getFarPlane());
```
