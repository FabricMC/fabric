package net.fabricmc.fabric.api.loot.v1.visitor;

public abstract class LootTableVisitor {
	abstract void visitTable(LootNode.LootTableNode table);

	abstract void visitPool(LootNode.LootPoolNode pool);

	abstract void visitAlternativeEntry(LootNode.AlternativeEntryNode alternativeEntry);
}
