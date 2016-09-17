package de.ma.tree;

import de.ma.lexer.Token;

public abstract class Atomic extends Node {
	public Atomic(Token tok){
		super(tok);
	}
}
