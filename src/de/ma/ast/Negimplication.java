package de.ma.ast;

import de.ma.lexer.Token;
import de.ma.treewalker.TreeWalker;

public class Negimplication extends Binary {

	public Negimplication(Token tok, Node n1, Node n2) {
		super(tok, n1, n2);
	}

	public <ReturnType, ArgumentType> ReturnType walk(TreeWalker<ReturnType, ArgumentType> walker, ArgumentType arg) {
		return walker.walkNegimplicationNode(this, arg);
	}
	
	public Negimplication clone(){
		return new Negimplication(getToken(), getLeft().clone(), getRight().clone());
	}
}
