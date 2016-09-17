package de.ma.treewalker;

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

public abstract class TreeWalker <ReturnType, ArgumentType>{
	
	public ReturnType walk (Node node, ArgumentType arg) {
		return node.walk(this, arg);
	}

	public abstract ReturnType walkBiconditionalNode(Biconditional node, ArgumentType arg);
	public abstract ReturnType walkImplicationNode(Implication node, ArgumentType arg);
	public abstract ReturnType walkNegimplicationNode(Negimplication node, ArgumentType arg);
	public abstract ReturnType walkXorNode(Xor node, ArgumentType arg);
	public abstract ReturnType walkOrNode(Or node, ArgumentType arg);
	public abstract ReturnType walkAndNode(And node, ArgumentType arg);
	public abstract ReturnType walkNotNode(Not node, ArgumentType arg);
	public abstract ReturnType walkBoxNode(Box node, ArgumentType arg);
	public abstract ReturnType walkDiamondNode(Diamond node, ArgumentType arg);
	public abstract ReturnType walkVariableNode(Variable node, ArgumentType arg);
	public abstract ReturnType walkConstantNode(Constant node, ArgumentType arg);
}
