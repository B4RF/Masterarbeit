package de.ma.tree;

import de.ma.lexer.Token;
import de.ma.treewalker.TreeWalker;

public class Variable extends Atomic{

	public Variable(Token tok) {
		super(tok);
	}

	public <ReturnType, ArgumentType> ReturnType walk(TreeWalker<ReturnType, ArgumentType> walker, ArgumentType arg) {
		return walker.walkVariableNode(this, arg);
	}
	
	public Variable clone(){
		return new Variable(getToken());
	}
}
