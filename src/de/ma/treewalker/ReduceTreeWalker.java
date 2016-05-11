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

public class ReduceTreeWalker extends TreeWalker<Node, Node> {

	@Override
	public Node walkBiconditionalNode(Biconditional node, Node arg) {
		node.setLeft(walk(node.getLeft(), null));
		node.setRight(walk(node.getRight(), null));
		
		Boolean isLeftConst = node.getLeft().getClass() == Constant.class;
		Boolean isRightConst = node.getRight().getClass() == Constant.class;
		
		/*
		 * truth table
		 * 		|	b	0	1
		 * -----|-------------
		 * a	|	<->	~a	a
		 * 0	|	~b	1	0
		 * 1	|	b	0	1
		 */
		
		if(isLeftConst || isRightConst){
			if(isLeftConst){
				Constant leftChild = (Constant) node.getLeft();
				
				if(isRightConst){	// beides konstanten
					Constant rightChild = (Constant) node.getRight();

					Constant c = new Constant(null, true);
					c.setValue((leftChild.getValue() && rightChild.getValue()) || (!leftChild.getValue() && !rightChild.getValue()));
					return c;
				}else{	// nur links konstante
					if(leftChild.getValue()){
						return node.getRight();
					}else{
						return walk(new Not(new Token('~'), node.getRight()), null);
					}
				}					
			}else{ // nur rechts konstante
				Constant rightChild = (Constant) node.getRight();
				
				if(rightChild.getValue()){
					return node.getLeft();
				}else{
					return walk(new Not(new Token('~'), node.getLeft()), null);
				}
			}
		}else{
			return node;
		}
	}

	@Override
	public Node walkImplicationNode(Implication node, Node arg) {
		node.setLeft(walk(node.getLeft(), null));
		node.setRight(walk(node.getRight(), null));
		
		Boolean isLeftConst = node.getLeft().getClass() == Constant.class;
		Boolean isRightConst = node.getRight().getClass() == Constant.class;
		
		/*
		 * truth table
		 * 		|	b	0	1
		 * -----|-------------
		 * a	|	->	~a	1
		 * 0	|	1	1	1
		 * 1	|	b	0	1
		 */
		
		if(isLeftConst || isRightConst){
			if(isLeftConst){
				Constant leftChild = (Constant) node.getLeft();
				
				if(isRightConst){	// beides konstanten
					Constant rightChild = (Constant) node.getRight();
					
					Constant c = new Constant(null, true);
					c.setValue(!leftChild.getValue() || rightChild.getValue());
					return c;
				}else{	// nur links konstante
					if(leftChild.getValue()){
						return node.getRight();
					}else{
						return new Constant(new Token('1'), true);
					}
				}					
			}else{ // nur rechts konstante
				Constant rightChild = (Constant) node.getRight();
				
				if(rightChild.getValue()){
					return new Constant(new Token('1'), true);
				}else{
					return walk(new Not(new Token('~'), node.getLeft()), null);
				}
			}
		}else{
			return node;
		}
	}

	@Override
	public Node walkNegimplicationNode(Negimplication node, Node arg) {
		node.setLeft(walk(node.getLeft(), null));
		node.setRight(walk(node.getRight(), null));
		
		Boolean isLeftConst = node.getLeft().getClass() == Constant.class;
		Boolean isRightConst = node.getRight().getClass() == Constant.class;
		
		/*
		 * truth table
		 * 		|	b	0	1
		 * -----|-------------
		 * a	|	~>	a	0
		 * 0	|	0	0	0
		 * 1	|	~b	1	0
		 */
		
		if(isLeftConst || isRightConst){
			if(isLeftConst){
				Constant leftChild = (Constant) node.getLeft();
				
				if(isRightConst){	// beides konstanten
					Constant rightChild = (Constant) node.getRight();

					Constant c = new Constant(null, true);
					c.setValue(leftChild.getValue() && !rightChild.getValue());
					return c;
				}else{	// nur links konstante
					if(leftChild.getValue()){
						return walk(new Not(new Token('~'), node.getRight()), null);
					}else{
						return new Constant(new Token('0'), false);
					}
				}					
			}else{ // nur rechts konstante
				Constant rightChild = (Constant) node.getRight();
				
				if(rightChild.getValue()){
					return new Constant(new Token('0'), false);
				}else{
					return node.getLeft();
				}
			}
		}else{
			return node;
		}
	}

	@Override
	public Node walkXorNode(Xor node, Node arg) {
		node.setLeft(walk(node.getLeft(), null));
		node.setRight(walk(node.getRight(), null));
		
		Boolean isLeftConst = node.getLeft().getClass() == Constant.class;
		Boolean isRightConst = node.getRight().getClass() == Constant.class;
		
		/*
		 * truth table
		 * 		|	b	0	1
		 * -----|-------------
		 * a	|	+	a	~a
		 * 0	|	b	0	1
		 * 1	|	~b	1	0
		 */
		
		if(isLeftConst || isRightConst){
			if(isLeftConst){
				Constant leftChild = (Constant) node.getLeft();
				
				if(isRightConst){	// beides konstanten
					Constant rightChild = (Constant) node.getRight();

					Constant c = new Constant(null, true);
					c.setValue(leftChild.getValue() != rightChild.getValue());
					return c;
				}else{	// nur links konstante
					if(leftChild.getValue()){
						return walk(new Not(new Token('~'), node.getRight()), null);
					}else{
						return node.getRight();
					}
				}					
			}else{ // nur rechts konstante
				Constant rightChild = (Constant) node.getRight();
				
				if(rightChild.getValue()){
					return walk(new Not(new Token('~'), node.getLeft()), null);
				}else{
					return node.getLeft();
				}
			}
		}else{
			return node;
		}
	}

	@Override
	public Node walkOrNode(Or node, Node arg) {
		node.setLeft(walk(node.getLeft(), null));
		node.setRight(walk(node.getRight(), null));
		
		Boolean isLeftConst = node.getLeft().getClass() == Constant.class;
		Boolean isRightConst = node.getRight().getClass() == Constant.class;
		
		/*
		 * truth table
		 * 		|	b	0	1
		 * -----|-------------
		 * a	|	|	a	1
		 * 0	|	b	0	1
		 * 1	|	1	1	1
		 */
		
		if(isLeftConst || isRightConst){
			if(isLeftConst){
				Constant leftChild = (Constant) node.getLeft();
				
				if(isRightConst){	// beides konstanten
					Constant rightChild = (Constant) node.getRight();

					Constant c = new Constant(null, true);
					c.setValue(leftChild.getValue() || rightChild.getValue());
					return c;
				}else{	// nur links konstante
					if(leftChild.getValue()){
						return new Constant(new Token('1'), true);
					}else{
						return node.getRight();
					}
				}					
			}else{ // nur rechts konstante
				Constant rightChild = (Constant) node.getRight();
				
				if(rightChild.getValue()){
					return new Constant(new Token('1'), true);
				}else{
					return node.getLeft();
				}
			}
		}else{
			return node;
		}
	}

	@Override
	public Node walkAndNode(And node, Node arg) {
		node.setLeft(walk(node.getLeft(), null));
		node.setRight(walk(node.getRight(), null));
		
		Boolean isLeftConst = node.getLeft().getClass() == Constant.class;
		Boolean isRightConst = node.getRight().getClass() == Constant.class;
		
		/*
		 * truth table
		 * 		|	b	0	1
		 * -----|-------------
		 * a	|	&	0	a
		 * 0	|	0	0	0
		 * 1	|	b	0	1
		 */
		
		if(isLeftConst || isRightConst){
			if(isLeftConst){
				Constant leftChild = (Constant) node.getLeft();
				
				if(isRightConst){	// beides konstanten
					Constant rightChild = (Constant) node.getRight();

					Constant c = new Constant(null, true);
					c.setValue(leftChild.getValue() && rightChild.getValue());
					return c;
				}else{	// nur links konstante
					if(leftChild.getValue()){
						return node.getRight();
					}else{
						return new Constant(new Token('0'), false);
					}
				}					
			}else{ // nur rechts konstante
				Constant rightChild = (Constant) node.getRight();
				
				if(rightChild.getValue()){
					return node.getLeft();
				}else{
					return new Constant(new Token('0'), false);
				}
			}
		}else{
			return node;
		}
	}

	@Override
	public Node walkNotNode(Not node, Node arg) {
		node.setNode(walk(node.getNode(), null));
		
		if(node.getNode().getClass() == Constant.class){
			Constant child = (Constant) node.getNode();
			child.setValue(!child.getValue());
			return child;
		}else if(node.getNode().getClass() == Not.class){
			Not child = (Not) node.getNode();
			return child.getNode();
		}else{
			return node;
		}
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
