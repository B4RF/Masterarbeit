package de.ma.ast;

import de.ma.lexer.Token;
import de.ma.treewalker.TreeWalker;

public class Diamond extends Unary {

	public Diamond(Token tok, Node n) {
		super(tok, n);
	}

	public <ReturnType, ArgumentType> ReturnType walk(TreeWalker<ReturnType, ArgumentType> walker, ArgumentType arg) {
		return walker.walkDiamondNode(this, arg);
	}
	
	public Diamond clone(){
		return new Diamond(getToken(), getNode().clone());
	}
}
