package de.ma.tree;

import de.ma.lexer.Token;

public abstract class Unary extends Node {
	Node node;

	public Unary(Token tok, Node n) {
		super(tok);
		node = n;
	}
	
	public Unary(Unary u){
		super(u.getToken());
		node = u.getNode();
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}
}
