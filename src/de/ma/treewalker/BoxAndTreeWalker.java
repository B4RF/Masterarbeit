package de.ma.treewalker;

import de.ma.ast.And;
import de.ma.ast.Biconditional;
import de.ma.ast.Box;
import de.ma.ast.Constant;
import de.ma.ast.Diamond;
import de.ma.ast.Implication;
import de.ma.ast.Negimplication;
import de.ma.ast.Node;
import de.ma.ast.Not;
import de.ma.ast.Or;
import de.ma.ast.Variable;
import de.ma.ast.Xor;
import de.ma.lexer.Token;

public class BoxAndTreeWalker extends TreeWalker<Node, Node> {

	@Override
	public Node walkBiconditionalNode(Biconditional node, Node arg) {
		node.setLeft(walk(node.getLeft(), null));
		node.setRight(walk(node.getRight(), null));
		// a<->b: (a&b)|(~a&~b): ~(~(a&b)&~(~a&~b))
		And innerLeftAnd = new And(new Token('&'), node.getLeft(), node.getRight());
		And innerRightAnd = new And(new Token('&'), new Not(new Token('~'), node.getLeft()), new Not(new Token('~'), node.getRight()));
		And and = new And(new Token('&'), new Not(new Token('~'), innerLeftAnd), new Not(new Token('~'), innerRightAnd));
		return new Not(new Token('~'), and);
	}

	@Override
	public Node walkImplicationNode(Implication node, Node arg) {
		node.setLeft(walk(node.getLeft(), null));
		node.setRight(walk(node.getRight(), null));
		// a->b: ~a|b: ~(a&~b)
		And and = new And(new Token('&'), node.getLeft(), new Not(new Token('~'), node.getRight()));
		return new Not(new Token('~'), and);
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
		// a+b: (~a&b)|(a&~b): ~(~(~a&b)&~(a&~b))
		And innerLeftAnd = new And(new Token('&'), new Not(new Token('~'), node.getLeft()), node.getRight());
		And innerRightAnd = new And(new Token('&'), node.getLeft(), new Not(new Token('~'), node.getRight()));
		And and = new And(new Token('&'), new Not(new Token('~'), innerLeftAnd), new Not(new Token('~'), innerRightAnd));
		return new Not(new Token('~'), and);
	}

	@Override
	public Node walkOrNode(Or node, Node arg) {
		node.setLeft(walk(node.getLeft(), null));
		node.setRight(walk(node.getRight(), null));
		// a|b: ~(~a&~b)
		And and = new And(new Token('&'), new Not(new Token('~'), node.getLeft()), new Not(new Token('~'), node.getRight()));
		return new Not(new Token('~'), and);
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
		// ~#~ statt $
		Not not = new Not(new Token('~'), new Box(new Token('#'), new Not(new Token('~'), node.getNode())));
		return not;
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
