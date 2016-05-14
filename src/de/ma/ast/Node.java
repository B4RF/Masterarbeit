package de.ma.ast;

import java.util.ArrayList;

import de.ma.lexer.Token;
import de.ma.lexer.Word;
import de.ma.treewalker.BoxAndTreeWalker;
import de.ma.treewalker.IntWithMax;
import de.ma.treewalker.MaxDegreeTreeWalker;
import de.ma.treewalker.ModalDepthTreeWalker;
import de.ma.treewalker.ReduceTreeWalker;
import de.ma.treewalker.TreeWalker;

public abstract class Node implements Cloneable {
	Token op;

	public Node(Token tok) {
		op = tok;
	}
	
	public Token getToken(){
		return op;
	}
	
	public abstract <ReturnType, ArgumentType> ReturnType walk(TreeWalker<ReturnType, ArgumentType> walker, ArgumentType arg);
	
	public abstract Node clone();

	public int getModalDepth(){
		ModalDepthTreeWalker mdtw = new ModalDepthTreeWalker();
		Node root = this.clone();
		
		return mdtw.walk(root, 0);
	}
	
	public int getMaxDegree(){
		//TODO calculate max diamond/box in a single world
		MaxDegreeTreeWalker mdtw = new MaxDegreeTreeWalker();
		IntWithMax iwm = mdtw.walk(this, null);
		
		return Math.max(iwm.getCurrentValue(), iwm.getMaxValue());
	}
	
	public boolean mlSat() {
		Node root = this.clone();
		
		BoxAndTreeWalker batw = new BoxAndTreeWalker();
		root = batw.walk(root, null);
		ReduceTreeWalker rtw = new ReduceTreeWalker();
		root = rtw.walk(root, null);

		ArrayList<Node> trues = new ArrayList<Node>();
		trues.add(root);

//		StringTreeWalker stw = new StringTreeWalker();
//		System.out.println(stw.walk(root, ""));
		
		return worldIter(trues, new ArrayList<Node>(), new ArrayList<Node>(), new ArrayList<Node>());
	}

	private static boolean worldIter(ArrayList<Node> trues,
			ArrayList<Node> falses, ArrayList<Node> nextTrues,
			ArrayList<Node> nextFalses) {
		Node node = null;

		for (Node n : trues) {
			if (!n.getClass().equals(Constant.class)
					&& !n.getClass().equals(Variable.class)) {
				node = n;
				break;
			}
		}

		if (node != null) {
			if (node.getClass().equals(Not.class)) {
				Not not = (Not) node;
				trues.remove(node);
				falses.add(not.getNode());
				return worldIter(trues, falses, nextTrues, nextFalses);
			} else if (node.getClass().equals(And.class)) {
				And and = (And) node;
				trues.remove(and);
				trues.add(and.getLeft());
				trues.add(and.getRight());
				return worldIter(trues, falses, nextTrues, nextFalses);
			} else if (node.getClass().equals(Box.class)) {
				Box box = (Box) node;
				trues.remove(box);
				nextTrues.add(box.getNode());
				return worldIter(trues, falses, nextTrues, nextFalses);
			}
		}

		for (Node n : falses) {
			if (!n.getClass().equals(Constant.class)
					&& !n.getClass().equals(Variable.class)) {
				node = n;
				break;
			}
		}

		if (node != null) {
			if (node.getClass().equals(Not.class)) {
				Not not = (Not) node;
				falses.remove(not);
				trues.add(not.getNode());
				return worldIter(trues, falses, nextTrues, nextFalses);
			} else if (node.getClass().equals(And.class)) {
				And and = (And) node;
				falses.remove(and);
				ArrayList<Node> falses2 = falses;
				falses.add(and.getLeft());
				falses2.add(and.getRight());
				return worldIter(trues, falses, nextTrues, nextFalses)
						|| worldIter(trues, falses2, nextTrues, nextFalses);
			} else if (node.getClass().equals(Box.class)) {
				Box box = (Box) node;
				falses.remove(box);
				nextFalses.add(box.getNode());
				return worldIter(trues, falses, nextTrues, nextFalses);
			}
		}
		
		// schnittmenge von t und f prüfen
		
		for (Node t : trues) {
			if (t.getClass().equals(Constant.class)) {
				for (Node f : falses) {
					if (f.getClass().equals(Constant.class)) {
						Constant c1 = (Constant) t;
						Constant c2 = (Constant) f;
						if(!(c1.getValue() ^ c2.getValue()))
							return false;
					}
				}
			} else if (t.getClass().equals(Variable.class)) {
				for (Node f : falses) {
					if(f.getClass().equals(Variable.class)){
						Variable v1 = (Variable) t;
						Variable v2 = (Variable) f;
						String var1 = ((Word) v1.getToken()).getVarible();
						String var2 = ((Word) v2.getToken()).getVarible();
						if(var1.equals(var2))
							return false;
					}
				}
			}
		}
		
		boolean satisfiable = true;
		ArrayList<Node> newNextFalses;
		
		for (Node n : nextFalses) {
			newNextFalses = new ArrayList<Node>();
			newNextFalses.add(n);
			satisfiable = satisfiable && worldIter(nextTrues, newNextFalses, new ArrayList<Node>(), new ArrayList<Node>());
		}

		return satisfiable;
	}
}
