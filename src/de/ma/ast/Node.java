package de.ma.ast;

import de.ma.lexer.Token;
import de.ma.treewalker.IntWithMax;
import de.ma.treewalker.MaxDegreeTreeWalker;
import de.ma.treewalker.ModalDepthTreeWalker;
import de.ma.treewalker.TreeWalker;

public abstract class Node implements Cloneable {
	Token op;

	public Node(Token tok) {
		op = tok;
	}

	public Token getToken() {
		return op;
	}

	public abstract <ReturnType, ArgumentType> ReturnType walk(TreeWalker<ReturnType, ArgumentType> walker,
			ArgumentType arg);

	public abstract Node clone();

	public int getModalDepth() {
		ModalDepthTreeWalker mdtw = new ModalDepthTreeWalker();
		Node root = this.clone();

		return mdtw.walk(root, 0);
	}

	public int getMaxDegree() {
		//TODO formel größe berechnen
		// calculate max diamond in a single world
		MaxDegreeTreeWalker mdtw = new MaxDegreeTreeWalker();
		IntWithMax iwm = mdtw.walk(this, null);

		return Math.max(iwm.getCurrentValue(), iwm.getMaxValue());
	}
}
