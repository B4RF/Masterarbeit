package de.ma.main;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.ma.ast.Node;
import de.ma.lexer.Lexer;
import de.ma.modal.GenerateGraphs;
import de.ma.modal.Graph;
import de.ma.modal.LabelGraph;
import de.ma.modal.Modal;
import de.ma.modal.Vertex;
import de.ma.parser.Parser;
import de.ma.treewalker.NNFTreeWalker;
import de.ma.treewalker.ReduceTreeWalker;
import de.ma.treewalker.StringTreeWalker;

public class Gui extends JFrame {
	private static final long serialVersionUID = 1L;
	static JPanel mainPanel = new JPanel();
	static JPanel formulaPanel = new JPanel();
	static JPanel conditionPanel1 = new JPanel();
	static JPanel conditionPanel2 = new JPanel();
	static JTextField formula = new JTextField();
	static JCheckBox minimal = new JCheckBox("minimal");
	static JCheckBox reflexive = new JCheckBox("reflexive");
	static JCheckBox transitive = new JCheckBox("transitive");
	static JCheckBox serial = new JCheckBox("serial");
	static JButton enumerate = new JButton("Enumerate");
	static JLabel satisfiable = new JLabel("-");

	static Lexer lex = new Lexer();
	static Parser parser = new Parser(lex);

	// Elemente der Auflistung
	static ArrayList<Modal> enumModals = new ArrayList<Modal>();
	int modalIndex;

	public Gui() {
		setTitle("Modallogic Enumerator");
		setResizable(false);

		mainPanel.setLayout(new GridLayout(4, 0));
		add(mainPanel);

		mainPanel.add(formulaPanel);
		mainPanel.add(conditionPanel1);
		mainPanel.add(conditionPanel2);
		mainPanel.add(enumerate);
		// mainPanel.add(satPanel);

		formulaPanel.setLayout(new GridLayout(1, 2));
		formulaPanel.add(new JLabel(" Insert modallogic formula:  "));
		formulaPanel.add(formula);
		formula.setHorizontalAlignment(SwingConstants.CENTER);

		conditionPanel1.setLayout(new GridLayout(1, 2));
		conditionPanel1.add(minimal);
		conditionPanel1.add(reflexive);
		conditionPanel2.setLayout(new GridLayout(1, 2));
		conditionPanel2.add(transitive);
		conditionPanel2.add(serial);

		// TODO remove?
		// satPanel.setLayout(new GridLayout(1, 2));
		// satPanel.add(new JLabel(" Is the formula satisfiable? "));
		// satPanel.add(satisfiable);
		// satisfiable.setHorizontalAlignment(SwingConstants.CENTER);

		this.pack();
		setLocationRelativeTo(null);
		setVisible(true);

		enumerate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				satisfiable.setText("-");
				enumModals.clear();

				final String input = formula.getText();

				if (input.equals(""))
					JOptionPane.showMessageDialog(new JFrame(), "Please insert a formula first.");
				else {
					
					Thread t = new Thread(new Runnable() {
						public void run() {
							Node root = parser.formula(input);

							//TODO a|~a need to fix
							updateSatisfiable(root);

							generateModals(root);

							if (minimal.isSelected())
								removeNonMinimal(root);

							if (enumModals.isEmpty())
								JOptionPane.showMessageDialog(new JFrame(), "No satisfying modal generated.");
							else{
								String formula = computeNNF(root);
								showModallist(formula);
							}
						}
					});
					
					t.start();
				}
			}
		});
	}

	public static void main(String[] args) throws IOException {
		new Gui();

	}

	protected void generateModals(Node root) {
		// System.out.println("\nFormel erfolgreich geparst");
		//
		// StringDepthTreeWalker sdtw = new StringDepthTreeWalker();
		// System.out.println(sdtw.walk(root, ""));

		int maxDegree = root.getMaxDegree();
		int diameter = root.getModalDepth() * 2;

		GenerateGraphs genG = new GenerateGraphs(maxDegree, diameter, reflexive.isSelected(), transitive.isSelected(),
				serial.isSelected());
		LabelGraph labelG = new LabelGraph();
		Graph currentGraph = genG.nextGraph();

		while (currentGraph != null) {
			Modal modal = new Modal(currentGraph);

			ArrayList<Modal> labeled = labelG.labelGraph(modal, root);

			for (Modal m : labeled) {
				enumModals.add(m);

				// TODO remove/nur fuer tests
				if (!m.mlMc(root))
					System.out.println("Failed to ModalCheck a generated Modal");
			}

			currentGraph = genG.nextGraph();
		}
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
		formula = formula.replace("~>", "\u2227\u00AC"); // TODO change?
		formula = formula.replace("~", "\u00AC");
		formula = formula.replace("<->", "\u2194");
		formula = formula.replace("->", "\u2192");
		formula = formula.replace("+", "\u2295");
		formula = formula.replace("$", "\u25CA");
		formula = formula.replace("#", "\u25A1");
		
		return formula;
	}

	protected void updateSatisfiable(Node root) {
		if (root.mlSat())
			satisfiable.setText("Yes");
		else
			satisfiable.setText("No");
	}

	private void removeNonMinimal(Node root) {
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

	private void showModallist(String formula) {
		final JFrame enumFrame = new JFrame();
		final JList<String> enumList = new JList<>();
		final JLabel imageLabel = new JLabel();
		final JLabel formulaLabel = new JLabel(formula, SwingConstants.CENTER);

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
					enumFrame.pack();
				}
			}
		});

		enumFrame.getContentPane().add(formulaLabel, BorderLayout.PAGE_START);
		enumFrame.getContentPane().add(new JScrollPane(enumList), BorderLayout.LINE_START);
		enumFrame.getContentPane().add(imageLabel, BorderLayout.CENTER);
		
		enumModals.get(0).draw(imageLabel);
		
		enumFrame.setTitle("Satisfying Models");
		enumFrame.pack();
		enumFrame.setLocationRelativeTo(null);
		enumFrame.setVisible(true);
	}
}
