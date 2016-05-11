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
import de.ma.lexer.Word;

public class NNFTreeWalker extends TreeWalker<Node, Boolean>{
	// the boolean indicates if the node needs to be negated
	
	@Override
	public Node walkBiconditionalNode(Biconditional node, Boolean arg) {
		// ~(a<->b) = a+b
		
		if(arg)
			return new Xor(new Token('+'), walk(node.getLeft(), false), walk(node.getRight(), false));
		
		node.setLeft(walk(node.getLeft(), false));
		node.setRight(walk(node.getRight(), false));
		
		return node;
	}

	@Override
	public Node walkImplicationNode(Implication node, Boolean arg) {
		// ~(a->b) = a~>b

		if(arg)
			return new Negimplication(Word.negimplication, walk(node.getLeft(), false), walk(node.getRight(), false));
		
		node.setLeft(walk(node.getLeft(), false));
		node.setRight(walk(node.getRight(), false));
		
		return node;
	}

	@Override
	public Node walkNegimplicationNode(Negimplication node, Boolean arg) {
		// ~(a->b) = a~>b

		if(arg)
			return new Implication(Word.implication, walk(node.getLeft(), false), walk(node.getRight(), false));
		
		node.setLeft(walk(node.getLeft(), false));
		node.setRight(walk(node.getRight(), false));
		
		return node;
	}

	@Override
	public Node walkXorNode(Xor node, Boolean arg) {
		// ~(a+b) = a<->b
		
		if(arg)
			return new Biconditional(Word.bicondition, walk(node.getLeft(), false), walk(node.getRight(), false));
		
		node.setLeft(walk(node.getLeft(), false));
		node.setRight(walk(node.getRight(), false));
		
		return node;
	}

	@Override
	public Node walkOrNode(Or node, Boolean arg) {
		// ~(a|b) = ~a&~b
		
		if(arg)
			return new And(new Token('&'), walk(node.getLeft(), true), walk(node.getRight(), true));
		
		node.setLeft(walk(node.getLeft(), false));
		node.setRight(walk(node.getRight(), false));
		
		return node;
	}

	@Override
	public Node walkAndNode(And node, Boolean arg) {
		// ~(a&b) = ~a|~b
		
		if(arg)
			return new Or(new Token('|'), walk(node.getLeft(), true), walk(node.getRight(), true));
		
		node.setLeft(walk(node.getLeft(), false));
		node.setRight(walk(node.getRight(), false));
		
		return node;
	}

	@Override
	public Node walkNotNode(Not node, Boolean arg) {
		// alter negation and return subnode
		return walk(node.getNode(), !arg);
	}

	@Override
	public Node walkBoxNode(Box node, Boolean arg) {
		// ~#a = $~a
		
		if(arg)
			return new Diamond(new Token('$'), walk(node.getNode(), true));
		
		node.setNode(walk(node.getNode(), false));
		
		return node;
	}

	@Override
	public Node walkDiamondNode(Diamond node, Boolean arg) {
		// ~$a = #~a
		
		if(arg)
			return new Box(new Token('#'), walk(node.getNode(), true));
		
		node.setNode(walk(node.getNode(), false));
		
		return node;
	}

	@Override
	public Node walkVariableNode(Variable node, Boolean arg) {

		if(arg)
			return new Not(new Token('~'), node);
		
		return node;
	}

	@Override
	public Node walkConstantNode(Constant node, Boolean arg) {
		// ~1 = 0, ~0 = 1
		
		if(arg){
			node.setValue(!node.getValue());
		}
		
		return node;
	}
}
