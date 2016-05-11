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
