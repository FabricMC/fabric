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

	interface AlternativeEntryNode extends LootNode {
		@Override
		default void visit(LootTableVisitor visitor) {
			visitor.visitAlternativeEntry(this);
		}
	}

	interface CombinedEntryNode extends LootNode {

	}
}
