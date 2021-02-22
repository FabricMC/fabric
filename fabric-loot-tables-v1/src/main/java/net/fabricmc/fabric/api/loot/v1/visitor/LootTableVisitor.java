package net.fabricmc.fabric.api.loot.v1.visitor;

public interface LootTableVisitor {
	void visitTable(LootNode.LootTableNode table);

	void visitPool(LootNode.LootPoolNode pool);

	void visitLootEntry(LootNode.LootEntryNode lootEntry);

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

	void visitAlternativeCondition(LootNode.AlternativeConditionNode alternativeCondition);

	void visitAndCondition(LootNode.AndConditionNode andConditionNode);

	void visitBlockStatePropertyCondition(LootNode.BlockStatePropertyConditionNode blockStatePropertyCondition);

	void visitDamageSourcePropertiesCondition(LootNode.DamageSourcePropertiesConditionNode damageSourcePropertiesCondition);

	void visitEntityPropertiesCondition(LootNode.EntityPropertiesConditionNode entityPropertiesConditionNode);

	void visitEntityScoresCondition(LootNode.EntityScoresConditionNode entityScoresCondition);

	void visitInvertedCondition(LootNode.InvertedConditionNode invertedCondition);

	void visitKilledByPlayerCondition(LootNode.KilledByPlayerConditionNode killedByPlayerConditionNode);

	void visitLocationCheckCondition(LootNode.LocationCheckConditionNode locationCheckConditionNode);

	void visitMatchToolCondition(LootNode.MatchToolConditionNode matchToolConditionNode);

	void visitRandomChanceCondition(LootNode.RandomChanceConditionNode randomChanceConditionNode);

	void visitRandomChanceWithLooting(LootNode.RandomChanceWithLootingConditionNode randomChanceWithLootingConditionNode);

	void visitReferenceCondition(LootNode.ReferenceConditionNode referenceConditionNode);

	void visitSurvivesExplosionCondition(LootNode.SurvivesExplosionConditionNode survivesExplosionConditionNode);

	void visitTableBonusCondition(LootNode.TableBonusConditionNode tableBonusConditionNode);

	void visitTimeCheckCondition(LootNode.TimeCheckConditionNode timeCheckConditionNode);

	void visitWeatherCheckCondition(LootNode.WeatherCheckConditionNode weatherCheckConditionNode);
}
