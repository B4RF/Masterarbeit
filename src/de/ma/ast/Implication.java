package de.ma.ast;

import de.ma.lexer.Token;
import de.ma.treewalker.TreeWalker;

public class Implication extends Binary {

	public Implication(Token tok, Node n1, Node n2) {
		super(tok, n1, n2);
	}

	public <ReturnType, ArgumentType> ReturnType walk(TreeWalker<ReturnType, ArgumentType> walker, ArgumentType arg) {
		return walker.walkImplicationNode(this, arg);
	}
	
	public Implication clone(){
		return new Implication(getToken(), getLeft().clone(), getRight().clone());
	}
}
