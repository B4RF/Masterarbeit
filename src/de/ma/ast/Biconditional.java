package de.ma.ast;

import de.ma.lexer.Token;
import de.ma.treewalker.TreeWalker;

public class Biconditional extends Binary {

	public Biconditional(Token tok, Node n1, Node n2) {
		super(tok, n1, n2);
	}

	public <ReturnType, ArgumentType> ReturnType walk(TreeWalker<ReturnType, ArgumentType> walker, ArgumentType arg) {
		return walker.walkBiconditionalNode(this, arg);
	}
	
	public Biconditional clone(){
		return new Biconditional(getToken(), getLeft().clone(), getRight().clone());
	}
}
