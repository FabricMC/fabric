package net.fabricmc.fabric.api.client.modelevents.v1.traversal;

import net.minecraft.client.util.math.MatrixStack;

public interface Traversable {
    void traverse(MatrixStack matrices, ModelVisitor visitor);
}
