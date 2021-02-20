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

	interface EntryNode extends LootNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitEntry(this);
		}
	}

	interface CombinedEntryNode extends EntryNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitCombinedEntry(this);
		}
	}

	interface LeafEntryNode extends EntryNode {
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
}
