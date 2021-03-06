package de.ma.modal;

import java.awt.BorderLayout;
import java.awt.Container;
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

import de.ma.tree.Node;
import de.ma.treewalker.NNFTreeWalker;
import de.ma.treewalker.ReduceTreeWalker;
import de.ma.treewalker.StringTreeWalker;
import de.ma.visuals.GeneratingTimeDiagram;

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

	int modalIndex = -1;

	Container pane;
	GeneratingTimeDiagram gtd;

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
				String selection = enumList.getSelectedValue();

				if (selection.equals("delayDiagram")) {
					pane.remove(0);
					pane.add(gtd.getDelayPanel(), BorderLayout.CENTER, 0);
				} else if (selection.equals("timeDiagram")) {
					pane.remove(0);
					pane.add(gtd.getTimePanel(), BorderLayout.CENTER, 0);
				} else {
					pane.remove(0);
					pane.add(imageLabel, BorderLayout.CENTER, 0);
					int index = Integer.parseInt(selection.substring(6));

					if (index != modalIndex) {
						modalIndex = index;
						Modal m = enumModals.get(modalIndex);
						m.draw(imageLabel);
					}
				}

				pane.repaint();
				pack();
			}
		});

		gtd = new GeneratingTimeDiagram();
		listModel.addElement("delayDiagram");
		listModel.addElement("timeDiagram");

		pane = this.getContentPane();
		pane.add(new JLabel(computeNNF(), SwingConstants.CENTER), BorderLayout.NORTH);
		pane.add(new JScrollPane(enumList), BorderLayout.WEST);
		pane.add(gtd.getDelayPanel(), BorderLayout.CENTER, 0);

		setTitle("Satisfying Models");
		pack();
		setLocationRelativeTo(null);
		setVisible(true);

		Generator g = new Generator(this);
		g.execute();
	}

	private class Generator extends SwingWorker<List<String>, Modal> {

		JFrame frame;

		public Generator(JFrame f) {
			this.frame = f;
		}

		@Override
		protected List<String> doInBackground() throws Exception {
			JProgressBar progress = new JProgressBar();
			getContentPane().add(progress, BorderLayout.SOUTH);

			int maxDegree = root.getMaxDegree() + 1;
			int diameter = root.getModalDepth() * 2;
			int maxVertices = root.getNumberDiamonds() + 1;

			GenerateGraphs genG = new GenerateGraphs(progress, maxDegree, diameter, maxVertices, reflexive, transitive,
					serial, partialReflexive, useOrbits);

			LabelGraph labelG = new LabelGraph();
			Graph currentGraph;
			gtd.start();

			while ((currentGraph = genG.nextGraph()) != null) {

				ArrayList<Modal> labeled = labelG.labelGraph(new Modal(currentGraph), root);
				ArrayList<ArrayList<ArrayList<String>>> fingerprints = new ArrayList<>();

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
						boolean dup = false;
						for (ArrayList<ArrayList<String>> fp : fingerprints) {
							if (m.hasFingerprint(fp, useOrbits))
								dup = true;
						}

						if (!dup) {
							fingerprints.add(m.getLabelFingerprint());
							gtd.update(1);
							publish(m);
						}
					}
				}
			}
			
			//TODO for testing
			//gtd.update(1);

			Thread.sleep(100); // takes care of the delay from publish
			if (enumModals.size() == 0) {
				frame.dispose();
				JOptionPane.showMessageDialog(frame, "No satisfying modal generated.");
			} else {
				frame.remove(progress);
			}
			return null;
		}

		@Override
		protected void process(List<Modal> chunks) {
			for (Modal m : chunks) {
				enumModals.add(m);
				listModel.addElement("Graph " + (enumModals.size() - 1));

				// if (enumModals.size() == 1) {
				// enumModals.get(0).draw(imageLabel);
				// pack();
				// }
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
		nnf = nnf.replace("~>", "\u219B");
		nnf = nnf.replace("~", "\u00AC");
		nnf = nnf.replace("<->", "\u2194");
		nnf = nnf.replace("->", "\u2192");
		nnf = nnf.replace("+", "\u2295");
		nnf = nnf.replace("$", "\u25CA");
		nnf = nnf.replace("#", "\u25A1");
		nnf = nnf.replace("0", "\u22A5");
		nnf = nnf.replace("1", "\u22A4");

		return nnf;
	}
}
