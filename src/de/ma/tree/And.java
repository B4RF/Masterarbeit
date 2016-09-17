package de.ma.tree;

import de.ma.lexer.Token;
import de.ma.treewalker.TreeWalker;

public class And extends Binary {

	public And(Token tok, Node n1, Node n2) {
		super(tok, n1, n2);
	}

	public <ReturnType, ArgumentType> ReturnType walk(TreeWalker<ReturnType, ArgumentType> walker, ArgumentType arg) {
		return walker.walkAndNode(this, arg);
	}
	
	public And clone(){
		return new And(getToken(), getLeft().clone(), getRight().clone());
	}
}
