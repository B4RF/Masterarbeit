package de.ma.ast;

import de.ma.lexer.Token;
import de.ma.treewalker.TreeWalker;

public class Box extends Unary {

	public Box(Token tok, Node n) {
		super(tok, n);
	}

	public <ReturnType, ArgumentType> ReturnType walk(TreeWalker<ReturnType, ArgumentType> walker, ArgumentType arg) {
		return walker.walkBoxNode(this, arg);
	}
	
	public Box clone(){
		return new Box(getToken(), getNode().clone());
	}
}
