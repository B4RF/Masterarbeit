package de.ma.modal;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.ma.ast.Node;
import de.ma.treewalker.NNFTreeWalker;
import de.ma.treewalker.ReduceTreeWalker;
import de.ma.treewalker.StringTreeWalker;

public class SatisfyingModals extends JFrame {
	private static final long serialVersionUID = 1L;

	Node root;
	boolean reflexive;
	boolean transitive;
	boolean serial;
	boolean partialReflexive;
	boolean useOrbits;

	ArrayList<Modal> enumModals = new ArrayList<>();
	DefaultListModel<String> listModel = new DefaultListModel<>();
	JList<String> enumList = new JList<>(listModel);
	JLabel imageLabel = new JLabel();

	int modalIndex;

	public SatisfyingModals(Node rt, boolean ref, boolean trans, boolean ser, boolean partRef, boolean orb) {
		this.root = rt;
		this.reflexive = ref;
		this.transitive = trans;
		this.serial = ser;
		this.partialReflexive = partRef;
		this.useOrbits = orb;

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

		this.getContentPane().add(new JLabel(computeNNF(), SwingConstants.CENTER), BorderLayout.NORTH);
		this.getContentPane().add(new JScrollPane(enumList), BorderLayout.WEST);
		this.getContentPane().add(imageLabel, BorderLayout.CENTER);

		setTitle("Satisfying Models");
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		Generator g = new Generator(this);
		g.execute();
	}
	
	private class Generator extends SwingWorker<List<String>, Modal>{
		
		JFrame frame;
		
		public  Generator(JFrame f){
			this.frame = f;
		}

		@Override
		protected List<String> doInBackground() throws Exception {
			JProgressBar progress = new JProgressBar();
			getContentPane().add(progress, BorderLayout.SOUTH);

			// TODO change
			int maxDegree = root.getMaxDegree();
			int diameter = root.getModalDepth() * 2;

			StringTreeWalker stw = new StringTreeWalker();
			String input = stw.walk(root, null);
			// maximal vertices for minimal graph is equals number of diamonds
			// plus one
			int maxVertices = input.length() - input.replace("$", "").length() + 1;

			GenerateGraphs genG = new GenerateGraphs(progress, maxDegree, diameter, maxVertices, reflexive, transitive, serial,
					partialReflexive, useOrbits);

			LabelGraph labelG = new LabelGraph(useOrbits);
			Graph currentGraph;

			while ((currentGraph = genG.nextGraph()) != null) {
				ArrayList<Modal> labeled = labelG.labelGraph(new Modal(currentGraph), root);

				boolean minimal;

				for (Modal m : labeled) {
					minimal = true;

					modalloop: for (Integer index : m.getVertices()) {
						Vertex v = m.getGraph().getVertex(index);

						for (Integer edge : v.getEdges()) {
							Modal mClone = m.clone();
							mClone.getGraph().removeEdge(index, edge);

							if (mClone.mlMc(root)) {
								minimal = false;
								break modalloop;
							}
						}
					}

					if (minimal) {
						publish(m);

						if (enumModals.size() == 1) {
							enumModals.get(0).draw(imageLabel);
							pack();
						}
					}
				}
			}
			
			if(enumModals.size() == 0){
				frame.dispose();
				JOptionPane.showMessageDialog(frame, "No satisfying modal generated.");
			}else{
				frame.remove(progress);
			}
			return null;
		}

		@Override
		protected void process(List<Modal> chunks) {
			for (Modal m : chunks) {
				enumModals.add(m);
				listModel.addElement("Graph " + (enumModals.size() - 1));
			}
		}
	}

	private String computeNNF() {
		ReduceTreeWalker rtw = new ReduceTreeWalker();
		root = rtw.walk(root, null);
		NNFTreeWalker ntw = new NNFTreeWalker();
		root = ntw.walk(root, false);

		StringTreeWalker stw = new StringTreeWalker();
		String nnf = stw.walk(root, "");

		nnf = nnf.replace("&", "\u2227");
		nnf = nnf.replace("|", "\u2228");
		nnf = nnf.replace("~>", "\u2227\u00AC"); // TODO change?
		nnf = nnf.replace("~", "\u00AC");
		nnf = nnf.replace("<->", "\u2194");
		nnf = nnf.replace("->", "\u2192");
		nnf = nnf.replace("+", "\u2295");
		nnf = nnf.replace("$", "\u25CA");
		nnf = nnf.replace("#", "\u25A1");

		return nnf;
	}
}
