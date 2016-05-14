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

public class MaxDegreeTreeWalker extends TreeWalker<IntWithMax, Integer>{

	@Override
	public IntWithMax walkBiconditionalNode(Biconditional node, Integer arg) {
		IntWithMax left = walk(node.getLeft(), arg);
		IntWithMax right = walk(node.getRight(), arg);
		IntWithMax iwm = new IntWithMax(left.getCurrentValue()+right.getCurrentValue(), Math.max(left.getMaxValue(), right.getMaxValue()));
		
		return iwm;
	}

	@Override
	public IntWithMax walkImplicationNode(Implication node, Integer arg) {
		IntWithMax left = walk(node.getLeft(), arg);
		IntWithMax right = walk(node.getRight(), arg);
		IntWithMax iwm = new IntWithMax(left.getCurrentValue()+right.getCurrentValue(), Math.max(left.getMaxValue(), right.getMaxValue()));
		
		return iwm;
	}

	@Override
	public IntWithMax walkNegimplicationNode(Negimplication node, Integer arg) {
		IntWithMax left = walk(node.getLeft(), arg);
		IntWithMax right = walk(node.getRight(), arg);
		IntWithMax iwm = new IntWithMax(left.getCurrentValue()+right.getCurrentValue(), Math.max(left.getMaxValue(), right.getMaxValue()));
		
		return iwm;
	}

	@Override
	public IntWithMax walkXorNode(Xor node, Integer arg) {
		IntWithMax left = walk(node.getLeft(), arg);
		IntWithMax right = walk(node.getRight(), arg);
		IntWithMax iwm = new IntWithMax(left.getCurrentValue()+right.getCurrentValue(), Math.max(left.getMaxValue(), right.getMaxValue()));
		
		return iwm;
	}

	@Override
	public IntWithMax walkOrNode(Or node, Integer arg) {
		IntWithMax left = walk(node.getLeft(), arg);
		IntWithMax right = walk(node.getRight(), arg);
		IntWithMax iwm = new IntWithMax(left.getCurrentValue()+right.getCurrentValue(), Math.max(left.getMaxValue(), right.getMaxValue()));
		
		return iwm;
	}

	@Override
	public IntWithMax walkAndNode(And node, Integer arg) {
		IntWithMax left = walk(node.getLeft(), arg);
		IntWithMax right = walk(node.getRight(), arg);
		IntWithMax iwm = new IntWithMax(left.getCurrentValue()+right.getCurrentValue(), Math.max(left.getMaxValue(), right.getMaxValue()));
		
		return iwm;
	}

	@Override
	public IntWithMax walkNotNode(Not node, Integer arg) {
		
		return walk(node.getNode(), arg);
	}

	@Override
	public IntWithMax walkBoxNode(Box node, Integer arg) {
		IntWithMax iwm = walk(node.getNode(), arg);
		iwm.setMaxValue(Math.max(iwm.getCurrentValue(), iwm.getMaxValue()));
		iwm.setCurrentValue(0);
		
		return iwm;
	}

	@Override
	public IntWithMax walkDiamondNode(Diamond node, Integer arg) {
		IntWithMax iwm = walk(node.getNode(), arg);
		iwm.setMaxValue(Math.max(iwm.getCurrentValue(), iwm.getMaxValue()));
		iwm.setCurrentValue(1);
		
		return iwm;
	}

	@Override
	public IntWithMax walkVariableNode(Variable node, Integer arg) {

		return new IntWithMax(0, 0);
	}

	@Override
	public IntWithMax walkConstantNode(Constant node, Integer arg) {
		
		return new IntWithMax(0, 0);
	}
}
