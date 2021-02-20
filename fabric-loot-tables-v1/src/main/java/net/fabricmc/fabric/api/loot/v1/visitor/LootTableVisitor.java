package net.fabricmc.fabric.api.loot.v1.visitor;

public interface LootTableVisitor {
	void visitTable(LootNode.LootTableNode table);

	void visitPool(LootNode.LootPoolNode pool);

	void visitEntry(LootNode.EntryNode entry);

	void visitCombinedEntry(LootNode.CombinedEntryNode combinedEntry);

	void visitLeafEntry(LootNode.LeafEntryNode leafEntry);

	void visitAlternativeEntry(LootNode.AlternativeEntryNode alternativeEntry);

	void visitDynamicEntry(LootNode.DynamicEntryNode dynamicEntry);

	void visitEmptyEntry(LootNode.EmptyEntryNode emptyEntry);

	void visitGroupEntry(LootNode.GroupEntryNode groupEntry);

	void visitItemEntry(LootNode.ItemEntryNode itemEntry);

	void visitLootTableEntry(LootNode.LootTableEntryNode lootTableEntry);

	void visitSequenceEntry(LootNode.SequenceEntryNode sequenceEntry);

	void visitTagEntry(LootNode.TagEntryNode tagEntry);

	void visitLootCondition(LootNode.LootConditionNode lootCondition);
}
