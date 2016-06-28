package de.ma.modal;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.ma.ast.Node;
import de.ma.treewalker.NNFTreeWalker;
import de.ma.treewalker.ReduceTreeWalker;
import de.ma.treewalker.StringTreeWalker;

public class SatisfyingModals extends JFrame{
	private static final long serialVersionUID = 1L;
	
	ArrayList<Modal> enumModals = new ArrayList<>();
	Node root;
	String formula;
	boolean reflexive;
	boolean transitive;
	boolean serial;
	boolean partialReflexive;
	boolean useOrbits;
	
	JList<String> enumList = new JList<>();
	JLabel imageLabel = new JLabel();

	int modalIndex;
	
	public SatisfyingModals(Node rt, boolean ref, boolean trans, boolean ser, boolean partRef, boolean orb) {
		this.root = rt;
		this.formula = computeNNF(root);
		
		this.reflexive = ref;
		this.transitive = trans;
		this.serial = ser;
		this.partialReflexive = partRef;
		this.useOrbits = orb;
		
		enumModals = generateModals(root);

		enumList.setModel(new AbstractListModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getElementAt(int i) {
				return "Graph " + i;
			}

			@Override
			public int getSize() {
				return enumModals.size();
			}
		});

		enumList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				int index = Integer.parseInt(enumList.getSelectedValue().substring(6));

				if (index != modalIndex) {
					modalIndex = index;
					Modal m = enumModals.get(modalIndex);
					m.draw(imageLabel);
					pack();
				}
			}
		});

		this.getContentPane().add(new JLabel(formula, SwingConstants.CENTER), BorderLayout.NORTH);
		this.getContentPane().add(new JScrollPane(enumList), BorderLayout.WEST);
		this.getContentPane().add(imageLabel, BorderLayout.CENTER);
		
		enumModals.get(0).draw(imageLabel);
		
		setTitle("Satisfying Models");
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private String computeNNF(Node root) {
		ReduceTreeWalker rtw = new ReduceTreeWalker();
		root = rtw.walk(root, null);
		NNFTreeWalker ntw = new NNFTreeWalker();
		root = ntw.walk(root, false);

		StringTreeWalker stw = new StringTreeWalker();
		String formula = stw.walk(root, "");

		formula = formula.replace("&", "\u2227");
		formula = formula.replace("|", "\u2228");
		formula = formula.replace("~>", "\u2227\u00AC"); //TODO change?
		formula = formula.replace("~", "\u00AC");
		formula = formula.replace("<->", "\u2194");
		formula = formula.replace("->", "\u2192");
		formula = formula.replace("+", "\u2295");
		formula = formula.replace("$", "\u25CA");
		formula = formula.replace("#", "\u25A1");
		
		return formula;
	}

	protected ArrayList<Modal> generateModals(Node root) {
		ArrayList<Modal> enumModals = new ArrayList<Modal>();
		int maxDegree = root.getMaxDegree();
		int diameter = root.getModalDepth() * 2;
		int maxVertices = 0;

		// TODO
		// maximal vertices for minimal graph is equals number of diamonds
		// plus one
		StringTreeWalker stw = new StringTreeWalker();
		String input = stw.walk(root, null);
		maxVertices = input.length() - input.replace("$", "").length() + 1;

		GenerateGraphs genG = new GenerateGraphs(maxDegree, diameter, maxVertices, reflexive,
				transitive, serial, partialReflexive, useOrbits);
		LabelGraph labelG = new LabelGraph(useOrbits);
		Graph currentGraph;

		while ((currentGraph = genG.nextGraph()) != null) {
			Modal modal = new Modal(currentGraph);

			ArrayList<Modal> labeled = labelG.labelGraph(modal, root);

			// TODO
			removeNonMinimal(labeled, root);

			for (Modal m : labeled) {
				enumModals.add(m);

				// if (!m.mlMc(root))
				// System.out.println("Error: Failed to ModalCheck a generated
				// Modal.");
			}
		}

		return enumModals;
	}

	private void removeNonMinimal(ArrayList<Modal> enumModals, Node root) {
		Iterator<Modal> iter = enumModals.iterator();
		while (iter.hasNext()) {
			Modal modal = iter.next();

			modalloop: for (Integer index : modal.getVertices()) {
				Vertex v = modal.getGraph().getVertex(index);

				for (Integer edge : v.getEdges()) {
					Modal m = modal.clone();
					m.getGraph().removeEdge(index, edge);

					if (m.mlMc(root)) {
						iter.remove();
						break modalloop;
					}
				}
			}
		}
	}
}
