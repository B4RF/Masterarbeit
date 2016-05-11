package de.ma.ast;

import de.ma.lexer.Token;
import de.ma.treewalker.TreeWalker;

public class Or extends Binary {
	public Or(Token tok, Node n1, Node n2) {
		super(tok, n1, n2);
	}

	public <ReturnType, ArgumentType> ReturnType walk(TreeWalker<ReturnType, ArgumentType> walker, ArgumentType arg) {
		return walker.walkOrNode(this, arg);
	}
	
	public Or clone(){
		return new Or(getToken(), getLeft().clone(), getRight().clone());
	}
}
