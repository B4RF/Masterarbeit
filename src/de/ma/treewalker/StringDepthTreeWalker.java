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
import de.ma.lexer.Word;

public class StringDepthTreeWalker extends TreeWalker<String, String> {

	@Override
	public String walkBiconditionalNode(Biconditional node, String arg) {
		return arg + " <->\n" + walk(node.getLeft(), "-" + arg)
				+ walk(node.getRight(), "-" + arg);
	}

	@Override
	public String walkImplicationNode(Implication node, String arg) {
		return arg + " ->\n" + walk(node.getLeft(), "-" + arg)
				+ walk(node.getRight(), "-" + arg);
	}

	@Override
	public String walkNegimplicationNode(Negimplication node, String arg) {
		return arg + " ~>\n" + walk(node.getLeft(), "-" + arg)
				+ walk(node.getRight(), "-" + arg);
	}

	@Override
	public String walkXorNode(Xor node, String arg) {
		return arg + " +\n" + walk(node.getLeft(), "-" + arg)
				+ walk(node.getRight(), "-" + arg);
	}

	@Override
	public String walkOrNode(Or node, String arg) {
		return arg + " |\n" + walk(node.getLeft(), "-" + arg)
				+ walk(node.getRight(), "-" + arg);
	}

	@Override
	public String walkAndNode(And node, String arg) {
		return arg + " &\n" + walk(node.getLeft(), "-" + arg)
				+ walk(node.getRight(), "-" + arg);
	}

	@Override
	public String walkNotNode(Not node, String arg) {
		return arg + " ~\n" + walk(node.getNode(), "-" + arg);
	}

	@Override
	public String walkBoxNode(Box node, String arg) {
		return arg + " #\n" + walk(node.getNode(), "-" + arg);
	}

	@Override
	public String walkDiamondNode(Diamond node, String arg) {
		return arg + " $\n" + walk(node.getNode(), "-" + arg);
	}

	@Override
	public String walkVariableNode(Variable node, String arg) {
		String var = ((Word) node.getToken()).getVarible();

		return arg + " " + var + "\n";
	}

	@Override
	public String walkConstantNode(Constant node, String arg) {
		if (node.getValue())
			return arg + " 1\n";
		else
			return arg + " 0\n";
	}

}
