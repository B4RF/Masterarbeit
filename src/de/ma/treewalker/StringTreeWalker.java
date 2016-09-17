package de.ma.treewalker;

import de.ma.lexer.Word;
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

public class StringTreeWalker extends TreeWalker<String, String> {

	@Override
	public String walkBiconditionalNode(Biconditional node, String arg) {
		return "(" + walk(node.getLeft(), null) + "<->"
				+ walk(node.getRight(), null) + ")";
	}

	@Override
	public String walkImplicationNode(Implication node, String arg) {
		return "(" + walk(node.getLeft(), null) + "->"
				+ walk(node.getRight(), null) + ")";
	}

	@Override
	public String walkNegimplicationNode(Negimplication node, String arg) {
		return "(" + walk(node.getLeft(), null) + "~>"
				+ walk(node.getRight(), null) + ")";
	}

	@Override
	public String walkXorNode(Xor node, String arg) {
		return "(" + walk(node.getLeft(), null) + "+" + walk(node.getRight(), null) + ")";
	}

	@Override
	public String walkOrNode(Or node, String arg) {
		return "(" + walk(node.getLeft(), null) + "|" + walk(node.getRight(), null) + ")";
	}

	@Override
	public String walkAndNode(And node, String arg) {
		return "(" + walk(node.getLeft(), null) + "&" + walk(node.getRight(), null) + ")";
	}

	@Override
	public String walkNotNode(Not node, String arg) {
		return "~" + walk(node.getNode(), null);
	}

	@Override
	public String walkBoxNode(Box node, String arg) {
		return "#" + walk(node.getNode(), null);
	}

	@Override
	public String walkDiamondNode(Diamond node, String arg) {
		return "$" + walk(node.getNode(), null);
	}

	@Override
	public String walkVariableNode(Variable node, String arg) {
		return ((Word) node.getToken()).getVarible();
	}

	@Override
	public String walkConstantNode(Constant node, String arg) {
		if(node.getValue())
			return "1";
		else
			return "0";
	}

}
