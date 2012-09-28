package org.jbpt.algo.tree.mdt;

public interface MDTVisitor {
	Object visitTrivial(MDTNode node, Object obj);
	Object visitComplete(MDTNode node, Object obj, int color);
	Object visitLinear(MDTNode node, Object obj);
	Object visitPrimitive(MDTNode node, Object obj);
}
