package de.ma.tree;

import de.ma.lexer.Token;
import de.ma.treewalker.FormulaSizeTreeWalker;
import de.ma.treewalker.ModalDepthTreeWalker;
import de.ma.treewalker.NumberDiamondsTreeWalker;
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
		FormulaSizeTreeWalker fstw = new FormulaSizeTreeWalker();

		return fstw.walk(this, null);
	}

	public int getNumberDiamonds() {
		NumberDiamondsTreeWalker ndtw = new NumberDiamondsTreeWalker();

		return ndtw.walk(this, 0);
	}
}
