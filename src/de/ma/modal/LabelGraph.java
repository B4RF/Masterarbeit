package de.ma.modal;

import java.util.ArrayList;

import de.ma.ast.And;
import de.ma.ast.Box;
import de.ma.ast.Diamond;
import de.ma.ast.Node;
import de.ma.ast.Not;
import de.ma.ast.Or;
import de.ma.ast.Variable;
import de.ma.lexer.Tag;
import de.ma.lexer.Word;
import de.ma.treewalker.AndOrTreeWalker;
import de.ma.treewalker.NNFTreeWalker;

public class LabelGraph {
	private boolean useOrbits;

	public LabelGraph(boolean orb) {
		this.useOrbits = orb;
	}

	public ArrayList<Modal> labelGraph(Modal modal, Node root) {
		AndOrTreeWalker aotw = new AndOrTreeWalker();
		root = aotw.walk(root, null);
		NNFTreeWalker nnf = new NNFTreeWalker();
		root = nnf.walk(root, false);

		return labelNNFGraph(modal, modal.getGraph().getInitVertex(), root);
	}

	private ArrayList<Modal> labelNNFGraph(Modal modal, Vertex vertex, Node root) {
		ArrayList<Modal> labeled = new ArrayList<>();
		modal = modal.clone();
		String prefix = "";

		switch (root.getToken().tag) {
		case '#':
			Box box = (Box) root;

			// box without successor is fulfilled
			if (vertex.getEdges().size() == 0)
				labeled.add(modal);

			ArrayList<Modal> combinedModals = new ArrayList<>();
			for (Integer index : vertex.getEdges()) {
				Vertex edge = modal.getGraph().getVertex(index);
				ArrayList<Modal> currentModals = labelNNFGraph(modal, edge, box.getNode());

				// if one edge doesn't satisfy, the box operator doesn't as well
				if (currentModals.isEmpty()) {
					combinedModals.clear();
					break;
				}

				// TODO check for isomorphic subgraphs

				if (combinedModals.isEmpty())
					combinedModals = currentModals;
				else
					combinedModals = combine(combinedModals, currentModals);
			}

			labeled.addAll(combinedModals);
			break;
		case '$':
			Diamond diam = (Diamond) root;

			for (Integer index : vertex.getEdges()) {
				Vertex edge = modal.getGraph().getVertex(index);

				labeled.addAll(labelNNFGraph(modal, edge, diam.getNode()));
			}

			break;
		case Tag.BICONDITION:
			// (a&b)|(~a&~b)
			// break;
		case Tag.IMPLICATION:
			// ~a|b
			// break;
		case Tag.NEGIMPLICATION:
			// a&~b
			// break;
		case '+':
			// (~a&b)|(a&~b)
			System.out.println("Fail: Wrong operator while labelling.");
			break;
		case '|':
			Or or = (Or) root;

			ArrayList<Modal> leftOr = labelNNFGraph(modal, vertex, or.getLeft());
			ArrayList<Modal> rightOr = labelNNFGraph(modal, vertex, or.getRight());

			for (Modal mLeft : leftOr) {
				labeled.add(mLeft);
			}
			for (Modal mRight : rightOr) {
				labeled.add(mRight);
			}
			for (Modal mLeft : leftOr) {
				for (Modal mRight : rightOr) {
					if (mLeft.isCompatible(mRight)) {
						Modal joined = mLeft.clone();
						joined.join(mRight);
						labeled.add(joined);
					}
				}
			}
			break;
		case '&':
			And and = (And) root;
			ArrayList<Modal> leftAnd = labelNNFGraph(modal, vertex, and.getLeft());
			ArrayList<Modal> rightAnd = labelNNFGraph(modal, vertex, and.getRight());

			for (Modal mLeft : leftAnd) {
				for (Modal mRight : rightAnd) {
					if (mLeft.isCompatible(mRight)) {
						Modal joined = mLeft.clone();
						joined.join(mRight);
						labeled.add(joined);
					}
				}
			}
			break;
		case '~':
			prefix = "~";
			Not not = (Not) root;
			root = not.getNode();

			// continues with var because formula is in NNF
		case Tag.VAR:
			Variable variable = (Variable) root;
			String var = prefix + ((Word) variable.getToken()).getVarible();

			if (modal.addVarToVertex(var, vertex.getIndex()))
				labeled.add(modal);
			break;
		default:
			throw new Error("Token error while labeling");
		}

		return labeled;
	}

	private ArrayList<Modal> combine(ArrayList<Modal> modalList1, ArrayList<Modal> modalList2) {
		ArrayList<Modal> combinedModals = new ArrayList<>();

		for (Modal m1 : modalList1) {
			for (Modal m2 : modalList2) {
				if (m1.isCompatible(m2)) {
					Modal joined = m1.clone();
					joined.join(m2);
					combinedModals.add(joined);
				}
			}
		}

		return combinedModals;
	}
}
