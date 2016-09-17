package de.ma.treewalker;

import de.ma.tree.And;
import de.ma.tree.Biconditional;
import de.ma.tree.Box;
import de.ma.tree.Constant;
import de.ma.tree.Diamond;
import de.ma.tree.Implication;
import de.ma.tree.Negimplication;
import de.ma.tree.Not;
import de.ma.tree.Or;
import de.ma.tree.Variable;
import de.ma.tree.Xor;

public class ModalDepthTreeWalker extends TreeWalker<Integer, Integer>{

	@Override
	public Integer walkBiconditionalNode(Biconditional node, Integer arg) {
		int left = walk(node.getLeft(), null);
		int right = walk(node.getRight(), null);
		
		return Math.max(left, right);
	}

	@Override
	public Integer walkImplicationNode(Implication node, Integer arg) {
		int left = walk(node.getLeft(), null);
		int right = walk(node.getRight(), null);
		
		return Math.max(left, right);
	}

	@Override
	public Integer walkNegimplicationNode(Negimplication node, Integer arg) {
		int left = walk(node.getLeft(), null);
		int right = walk(node.getRight(), null);
		
		return Math.max(left, right);
	}

	@Override
	public Integer walkXorNode(Xor node, Integer arg) {
		int left = walk(node.getLeft(), null);
		int right = walk(node.getRight(), null);
		
		return Math.max(left, right);
	}

	@Override
	public Integer walkOrNode(Or node, Integer arg) {
		int left = walk(node.getLeft(), null);
		int right = walk(node.getRight(), null);
		
		return Math.max(left, right);
	}

	@Override
	public Integer walkAndNode(And node, Integer arg) {
		int left = walk(node.getLeft(), null);
		int right = walk(node.getRight(), null);
		
		return Math.max(left, right);
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
		
		return 1+walk(node.getNode(), null);
	}

	@Override
	public Integer walkVariableNode(Variable node, Integer arg) {
		
		return 0;
	}

	@Override
	public Integer walkConstantNode(Constant node, Integer arg) {
		
		return 0;
	}

}
