package net.fabricmc.fabric.api.loot.v1.visitor;

public interface LootNode {
	void visit(LootTableVisitor visitor);

	interface LootTableNode extends LootNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitTable(this);
		}
	}

	interface LootPoolNode extends LootNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitPool(this);
		}
	}

	interface LootEntryNode extends LootNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitLootEntry(this);
		}
	}

	interface CombinedEntryNode extends LootEntryNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitCombinedEntry(this);
		}
	}

	interface LeafEntryNode extends LootEntryNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitLeafEntry(this);
		}
	}

	interface AlternativeEntryNode extends CombinedEntryNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitAlternativeEntry(this);
		}
	}

	interface DynamicEntryNode extends LeafEntryNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitDynamicEntry(this);
		}
	}

	interface EmptyEntryNode extends LeafEntryNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitEmptyEntry(this);
		}
	}

	interface GroupEntryNode extends CombinedEntryNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitGroupEntry(this);
		}
	}

	interface ItemEntryNode extends LeafEntryNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitItemEntry(this);
		}
	}

	interface LootTableEntryNode extends LeafEntryNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitLootTableEntry(this);
		}
	}

	interface SequenceEntryNode extends CombinedEntryNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitSequenceEntry(this);
		}
	}

	interface TagEntryNode extends LeafEntryNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitTagEntry(this);
		}
	}

	interface LootConditionNode extends LootNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitLootCondition(this);
		}
	}

	interface AlternativeConditionNode extends LootConditionNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitAlternativeCondition(this);
		}
	}

	// Not currently used.
	interface AndConditionNode extends LootConditionNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitAndCondition(this);
		}
	}

	interface BlockStatePropertyConditionNode extends LootConditionNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitBlockStatePropertyCondition(this);
		}
	}

	interface DamageSourcePropertiesConditionNode extends LootConditionNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitDamageSourcePropertiesCondition(this);
		}
	}

	interface EntityPropertiesConditionNode extends LootConditionNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitEntityPropertiesCondition(this);
		}
	}

	interface EntityScoresConditionNode extends LootConditionNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitEntityScoresCondition(this);
		}
	}

	interface InvertedConditionNode extends LootConditionNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitInvertedCondition(this);
		}
	}

	interface KilledByPlayerConditionNode extends LootConditionNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitKilledByPlayerCondition(this);
		}
	}

	interface LocationCheckConditionNode extends LootConditionNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitLocationCheckCondition(this);
		}
	}

	interface MatchToolConditionNode extends LootConditionNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitMatchToolCondition(this);
		}
	}

	interface RandomChanceConditionNode extends LootConditionNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitRandomChanceCondition(this);
		}
	}

	interface RandomChanceWithLootingConditionNode extends LootConditionNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitRandomChanceWithLooting(this);
		}
	}

	interface ReferenceConditionNode extends LootConditionNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitReferenceCondition(this);
		}
	}

	interface SurvivesExplosionConditionNode extends LootConditionNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitSurvivesExplosionCondition(this);
		}
	}

	interface TableBonusConditionNode extends LootConditionNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitTableBonusCondition(this);
		}
	}

	interface TimeCheckConditionNode extends LootConditionNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitTimeCheckCondition(this);
		}
	}

	interface WeatherCheckConditionNode extends LootConditionNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitWeatherCheckCondition(this);
		}
	}
}
