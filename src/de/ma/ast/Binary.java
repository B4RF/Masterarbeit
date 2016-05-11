package de.ma.ast;

import de.ma.lexer.Token;


public abstract class Binary extends Node {
	Node left, right;

	public Binary(Token tok, Node n1, Node n2) {
		super(tok);
		left = n1;
		right = n2;
	}
	
	public Binary(Binary b){
		super(b.getToken());
		left = b.getLeft();
		right = b.getRight();
	}

	public Node getLeft() {
		return left;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}
}
