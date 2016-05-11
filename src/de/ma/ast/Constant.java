package de.ma.ast;

import de.ma.lexer.Token;
import de.ma.treewalker.TreeWalker;

public class Constant extends Atomic {
	boolean value;

	public Constant(Token tok, boolean b) {
		super(tok);
		value = b;
	}

	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
		if(value)
			super.op = new Token('1');
		else
			super.op = new Token('0');
	}

	public <ReturnType, ArgumentType> ReturnType walk(TreeWalker<ReturnType, ArgumentType> walker, ArgumentType arg) {
		return walker.walkConstantNode(this, arg);
	}
	
	public Constant clone(){
		return new Constant(getToken(), getValue());
	}
}
