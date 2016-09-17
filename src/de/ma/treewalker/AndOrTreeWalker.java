package de.ma.treewalker;

import de.ma.lexer.Token;
import de.ma.tree.And;
import de.ma.tree.Biconditional;
import de.ma.tree.Box;
import de.ma.tree.Constant;
import de.ma.tree.Diamond;
import de.ma.tree.Implication;
import de.ma.tree.Negimplication;
import de.ma.tree.Node;
import de.ma.tree.Not;
import de.ma.tree.Or;
import de.ma.tree.Variable;
import de.ma.tree.Xor;

public class AndOrTreeWalker extends TreeWalker<Node, Node>{
	
	@Override
	public Node walkBiconditionalNode(Biconditional node, Node arg) {
		node.setLeft(walk(node.getLeft(), null));
		node.setRight(walk(node.getRight(), null));
		// a<->b: (a&b)|(~a&~b)
		And leftAnd = new And(new Token('&'), node.getLeft(), node.getRight());
		And rightAnd = new And(new Token('&'), new Not(new Token('~'), node.getLeft()), new Not(new Token('~'), node.getRight()));
		Or or = new Or(new Token('|'), leftAnd, rightAnd);
		return or;
	}

	@Override
	public Node walkImplicationNode(Implication node, Node arg) {
		node.setLeft(walk(node.getLeft(), null));
		node.setRight(walk(node.getRight(), null));
		// a->b: ~a|b
		Or or = new Or(new Token('|'), new Not(new Token('~'), node.getLeft()), node.getRight());
		return or;
	}

	@Override
	public Node walkNegimplicationNode(Negimplication node, Node arg) {
		node.setLeft(walk(node.getLeft(), null));
		node.setRight(walk(node.getRight(), null));
		// a~>b: a&~b
		And and = new And(new Token('&'), node.getLeft(), new Not(new Token('~'), node.getRight()));
		return and;
	}

	@Override
	public Node walkXorNode(Xor node, Node arg) {
		node.setLeft(walk(node.getLeft(), null));
		node.setRight(walk(node.getRight(), null));
		// a+b: (~a&b)|(a&~b)
		And leftAnd = new And(new Token('&'), new Not(new Token('~'), node.getLeft()), node.getRight());
		And rightAnd = new And(new Token('&'), node.getLeft(), new Not(new Token('~'), node.getRight()));
		Or or = new Or(new Token('|'), leftAnd, rightAnd);
		return or;
	}

	@Override
	public Node walkOrNode(Or node, Node arg) {
		node.setLeft(walk(node.getLeft(), null));
		node.setRight(walk(node.getRight(), null));
		return node;
	}

	@Override
	public Node walkAndNode(And node, Node arg) {
		node.setLeft(walk(node.getLeft(), null));
		node.setRight(walk(node.getRight(), null));
		return node;
	}

	@Override
	public Node walkNotNode(Not node, Node arg) {
		node.setNode(walk(node.getNode(), null));
		return node;
	}

	@Override
	public Node walkBoxNode(Box node, Node arg) {
		node.setNode(walk(node.getNode(), null));
		return node;
	}

	@Override
	public Node walkDiamondNode(Diamond node, Node arg) {
		node.setNode(walk(node.getNode(), null));
		return node;
	}

	@Override
	public Node walkVariableNode(Variable node, Node arg) {
		return node;
	}

	@Override
	public Node walkConstantNode(Constant node, Node arg) {
		return node;
	}
}
