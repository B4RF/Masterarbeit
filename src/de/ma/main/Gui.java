package de.ma.main;

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
	static JPanel minPanel = new JPanel();
	static JPanel nnfPanel = new JPanel();
	static JPanel satPanel = new JPanel();
	static JTextField formula = new JTextField();
	static JCheckBox minimal = new JCheckBox();
	static JButton enumerate = new JButton("Enumerate");
	static JLabel nnf = new JLabel("-");
	static JLabel satisfiable = new JLabel("-");

	static Lexer lex = new Lexer();
	static Parser parser = new Parser(lex);

	// Elemente der Auflistung
	static ArrayList<Modal> enumModals = new ArrayList<Modal>();
	static JFrame enumFrame = new JFrame();
	int modalIndex;

	public Gui() {
		setTitle("Modallogic Enumerator");
		setResizable(false);

		mainPanel.setLayout(new GridLayout(5, 0));
		add(mainPanel);

		mainPanel.add(formulaPanel);
		mainPanel.add(minPanel);
		mainPanel.add(enumerate);
		mainPanel.add(nnfPanel);
		mainPanel.add(satPanel);

		formulaPanel.setLayout(new GridLayout(1, 2));
		formulaPanel.add(new JLabel(" Insert modallogic formula: "));
		formulaPanel.add(formula);
		formula.setHorizontalAlignment(SwingConstants.CENTER);

		// TODO change JCheckbox
		minPanel.setLayout(new GridLayout(1, 2));
		minPanel.add(new JLabel(" Only minimal modals "));
		minPanel.add(minimal);

		nnfPanel.setLayout(new GridLayout(1, 2));
		nnfPanel.add(new JLabel(" Formula in NNF: "));
		nnfPanel.add(nnf);
		nnf.setHorizontalAlignment(SwingConstants.CENTER);

		//TODO remove?
		satPanel.setLayout(new GridLayout(1, 2));
		satPanel.add(new JLabel(" Is the formula satisfiable? "));
		satPanel.add(satisfiable);
		satisfiable.setHorizontalAlignment(SwingConstants.CENTER);

		this.pack();
		setLocationRelativeTo(null);
		setVisible(true);

		enumerate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				nnf.setText("-");
				satisfiable.setText("-");
				enumModals.clear();
				enumFrame.dispose();

				String input = formula.getText();

				if (input.equals(""))
					JOptionPane.showMessageDialog(new JFrame(), "Please insert a formula first.");
				else {
					Node root = parser.formula(input);

					updateNNF(root);

					//TODO a|~a need to fix
					updateSatisfiable(root);

					generateModals(root);

					if (minimal.isSelected())
						removeNonminModals(root);

					if (enumModals.isEmpty())
						JOptionPane.showMessageDialog(new JFrame(), "No satisfying modal generated.");
					else
						showModallist();
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

		GenerateGraphs genG = new GenerateGraphs(maxDegree, diameter);
		LabelGraph labelG = new LabelGraph();
		Graph currentGraph = genG.nextGraph();

		while (currentGraph != null) {
			Modal modal = new Modal(currentGraph);

			// enumModals.add(modal);

			ArrayList<Modal> labeled = labelG.labelGraph(modal, root);

			for (Modal m : labeled) {
				enumModals.add(m);

				//TODO remove/nur fuer tests
				if (!m.mlMc(root))
					System.out.println("Failed to ModalCheck a generated Modal");
			}

			currentGraph = genG.nextGraph();
		}
	}

	private void updateNNF(Node root) {
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
		nnf.setText(formula);
	}

	protected void updateSatisfiable(Node root) {
		if (root.mlSat())
			satisfiable.setText("Yes");
		else
			satisfiable.setText("No");
	}

	private void removeNonminModals(Node root) {
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

	private void showModallist() {
		enumFrame = new JFrame();
		JPanel enumPanel = new JPanel();
		final JList<String> enumList = new JList<>();
		final JLabel imageLabel = new JLabel();

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

		imageLabel.setSize(400, 300);

		if (enumModals.size() > 8)
			enumPanel.add(new JScrollPane(enumList));
		else
			enumPanel.add(enumList);

		enumPanel.add(imageLabel);
		enumModals.get(0).draw(imageLabel);

		enumFrame.add(enumPanel);

		enumFrame.setTitle("Satisfying Models");
		enumFrame.pack();
		enumFrame.setLocationRelativeTo(null);
		enumFrame.setVisible(true);
	}
}
