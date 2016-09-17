package de.ma.tree;

import de.ma.lexer.Token;
import de.ma.treewalker.TreeWalker;

public class Xor extends Binary{
	public Xor(Token tok, Node n1, Node n2) {
		super(tok, n1, n2);
	}

	public <ReturnType, ArgumentType> ReturnType walk(TreeWalker<ReturnType, ArgumentType> walker, ArgumentType arg) {
		return walker.walkXorNode(this, arg);
	}
	
	public Xor clone(){
		return new Xor(getToken(), getLeft().clone(), getRight().clone());
	}
}
