package de.ma.treewalker;

import de.ma.ast.And;
import de.ma.ast.Biconditional;
import de.ma.ast.Box;
import de.ma.ast.Constant;
import de.ma.ast.Diamond;
import de.ma.ast.Implication;
import de.ma.ast.Negimplication;
import de.ma.ast.Not;
import de.ma.ast.Or;
import de.ma.ast.Variable;
import de.ma.ast.Xor;

public class FormulaSizeTreeWalker extends TreeWalker<Integer, Integer>{

	@Override
	public Integer walkBiconditionalNode(Biconditional node, Integer arg) {
		int left = walk(node.getLeft(), null);
		int right = walk(node.getRight(), null);
		
		return left+right;
	}

	@Override
	public Integer walkImplicationNode(Implication node, Integer arg) {
		int left = walk(node.getLeft(), null);
		int right = walk(node.getRight(), null);
		
		return left+right;
	}

	@Override
	public Integer walkNegimplicationNode(Negimplication node, Integer arg) {
		int left = walk(node.getLeft(), null);
		int right = walk(node.getRight(), null);
		
		return left+right;
	}

	@Override
	public Integer walkXorNode(Xor node, Integer arg) {
		int left = walk(node.getLeft(), null);
		int right = walk(node.getRight(), null);
		
		return left+right;
	}

	@Override
	public Integer walkOrNode(Or node, Integer arg) {
		int left = walk(node.getLeft(), null);
		int right = walk(node.getRight(), null);
		
		return left+right;
	}

	@Override
	public Integer walkAndNode(And node, Integer arg) {
		int left = walk(node.getLeft(), null);
		int right = walk(node.getRight(), null);
		
		return left+right;
	}

	@Override
	public Integer walkNotNode(Not node, Integer arg) {

		return walk(node.getNode(), null);
	}

	@Override
	public Integer walkBoxNode(Box node, Integer arg) {
		
		return walk(node.getNode(), null);
	}

	@Override
	public Integer walkDiamondNode(Diamond node, Integer arg) {
		
		return walk(node.getNode(), null);
	}

	@Override
	public Integer walkVariableNode(Variable node, Integer arg) {
		
		return 1;
	}

	@Override
	public Integer walkConstantNode(Constant node, Integer arg) {
		
		return 0;
	}

}
