package de.ma.treewalker;

import java.util.ArrayList;
import java.util.HashSet;

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
import de.ma.modal.Modal;

public class MlMcTreeWalker extends TreeWalker<HashSet<Integer>, Modal> {

	@Override
	public HashSet<Integer> walkBiconditionalNode(Biconditional node, Modal arg) {
		HashSet<Integer> satLeftChild = walk(node.getLeft(), arg);
		HashSet<Integer> satRightChild = walk(node.getRight(), arg);
		HashSet<Integer> allVertices = arg.getGraph().getVertices();
		HashSet<Integer> allVertices2 = arg.getGraph().getVertices();

		// (a&b)|(~a&~b)

		allVertices.removeAll(satLeftChild); // ~a
		allVertices2.removeAll(satRightChild); // ~b
		allVertices.retainAll(allVertices2); // &
		
		satLeftChild.retainAll(satRightChild); // a&b
		
		allVertices.addAll(satLeftChild); // |
//		System.out.println(allVertices);
		return allVertices;
	}

	@Override
	public HashSet<Integer> walkImplicationNode(Implication node, Modal arg) {
		HashSet<Integer> satLeftChild = walk(node.getLeft(), arg);
		HashSet<Integer> satRightChild = walk(node.getRight(), arg);
		HashSet<Integer> allVertices = arg.getGraph().getVertices();
		
		// ~a|b
		
		allVertices.removeAll(satLeftChild); // ~a
		allVertices.addAll(satRightChild); // |b
//		System.out.println(allVertices);
		return allVertices;
	}

	@Override
	public HashSet<Integer> walkNegimplicationNode(Negimplication node, Modal arg) {
		HashSet<Integer> satLeftChild = walk(node.getLeft(), arg);
		HashSet<Integer> satRightChild = walk(node.getRight(), arg);
		HashSet<Integer> allVertices = arg.getGraph().getVertices();
		
		// a&~b
		
		allVertices.removeAll(satRightChild); // ~b
		allVertices.retainAll(satLeftChild); // &a
//		System.out.println(allVertices);
		return allVertices;
	}

	@Override
	public HashSet<Integer> walkXorNode(Xor node, Modal arg) {
		HashSet<Integer> satLeftChild = walk(node.getLeft(), arg);
		HashSet<Integer> satRightChild = walk(node.getRight(), arg);
		HashSet<Integer> allVertices = arg.getGraph().getVertices();
		HashSet<Integer> allVertices2 = arg.getGraph().getVertices();
		
		// (a&~b)|(~a&b)
		
		allVertices.removeAll(satRightChild); // ~b
		allVertices.retainAll(satLeftChild); // &a
		
		allVertices2.removeAll(satLeftChild); // ~a
		allVertices2.retainAll(satRightChild); // &b
		
		allVertices.addAll(allVertices2); // |
//		System.out.println(allVertices);
		return allVertices;
	}

	@Override
	public HashSet<Integer> walkOrNode(Or node, Modal arg) {
		HashSet<Integer> satLeftChild = walk(node.getLeft(), arg);
		HashSet<Integer> satRightChild = walk(node.getRight(), arg);
		
		satLeftChild.addAll(satRightChild);
//		System.out.println(satLeftChild);
		return satLeftChild;
	}

	@Override
	public HashSet<Integer> walkAndNode(And node, Modal arg) {
		HashSet<Integer> satLeftChild = walk(node.getLeft(), arg);
		HashSet<Integer> satRightChild = walk(node.getRight(), arg);

		satLeftChild.retainAll(satRightChild);
//		System.out.println(satLeftChild);
		return satLeftChild;
	}

	@Override
	public HashSet<Integer> walkNotNode(Not node, Modal arg) {
		HashSet<Integer> satChild = walk(node.getNode(), arg);
		HashSet<Integer> allVertices = arg.getGraph().getVertices();
		
		allVertices.removeAll(satChild);
//		System.out.println(allVertices);
		return allVertices;
	}

	@Override
	public HashSet<Integer> walkBoxNode(Box node, Modal arg) {
		HashSet<Integer> satChild = walk(node.getNode(), arg);
		HashSet<Integer> allVertices = arg.getGraph().getVertices();
		
		ArrayList<Integer> edges;
		HashSet<Integer> satRoot = new HashSet<>();
		
		boolean sat;
		for (Integer indexOrigin : allVertices) {
			sat = true;
			edges = arg.getGraph().getVertex(indexOrigin).getEdges();
			
			for (Integer indexTarget : edges) {
				// kein nachfolger darf formel nicht erfuellen
				if(!satChild.contains(indexTarget)){
					sat = false;
				}
			}
			
			if(sat)
				satRoot.add(indexOrigin);
		}
//		System.out.println(satRoot);
		return satRoot;
	}

	@Override
	public HashSet<Integer> walkDiamondNode(Diamond node, Modal arg) {
		HashSet<Integer> satChild = walk(node.getNode(), arg);
		HashSet<Integer> allVertices = arg.getGraph().getVertices();
		
		HashSet<Integer> satRoot = new HashSet<>();
		
		for (Integer indexOrigin : allVertices) {
			for (Integer indexTarget : satChild) {
				// wenigstens ein nachfolger muss formel erf�llen
				if(arg.getGraph().containsEdge(indexOrigin, indexTarget)){
					satRoot.add(indexOrigin);
					break;
				}
			}
		}
//		System.out.println(satRoot);
		return satRoot;
	}

	@Override
	public HashSet<Integer> walkVariableNode(Variable node, Modal arg) {
		String var = ((Word) node.getToken()).getVarible();
		HashSet<Integer> satVar = new HashSet<>();
		
		for (Integer v : arg.getVerticesWithVar(var)) {
			satVar.add(v);
		}
		
		return satVar;
	}

	@Override
	public HashSet<Integer> walkConstantNode(Constant node, Modal arg) {
		if(node.getValue()){
			return arg.getGraph().getVertices();
		}else{
			return new HashSet<>();
		}
	}
}
