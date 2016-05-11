package de.ma.ast;

import de.ma.lexer.Token;
import de.ma.treewalker.TreeWalker;

public class Bracket extends Node {

	public Bracket(Token tok) {
		super(tok);
	}

	@Override
	public <ReturnType, ArgumentType> ReturnType walk(
			TreeWalker<ReturnType, ArgumentType> walker, ArgumentType arg) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Bracket clone(){
		return new Bracket(getToken());
	}
}
