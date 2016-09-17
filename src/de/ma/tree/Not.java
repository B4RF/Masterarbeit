package de.ma.tree;

import de.ma.lexer.Token;
import de.ma.treewalker.TreeWalker;

public class Not extends Unary {

	public Not(Token tok, Node n) {
		super(tok, n);
	}

	public <ReturnType, ArgumentType> ReturnType walk(TreeWalker<ReturnType, ArgumentType> walker, ArgumentType arg) {
		return walker.walkNotNode(this, arg);
	}
	
	public Not clone(){
		return new Not(getToken(), getNode().clone());
	}
}
