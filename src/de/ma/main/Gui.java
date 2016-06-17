package de.ma.main;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.ma.ast.Node;
import de.ma.lexer.Lexer;
import de.ma.modal.GenerateGraphs;
import de.ma.modal.Graph;
import de.ma.modal.LabelGraph;
import de.ma.modal.Modal;
import de.ma.modal.SatisfyingModals;
import de.ma.modal.Vertex;
import de.ma.parser.Parser;

public class Gui extends JFrame {
	private static final long serialVersionUID = 1L;
	
	static JPanel mainPanel = new JPanel();
	static JPanel formulaPanel = new JPanel();
	static JPanel conditionPanel1 = new JPanel();
	static JPanel conditionPanel2 = new JPanel();
	static JTextField formula = new JTextField();
	static JCheckBox minimal = new JCheckBox("minimal", true);
	static JCheckBox reflexive = new JCheckBox("reflexive");
	static JCheckBox transitive = new JCheckBox("transitive");
	static JCheckBox serial = new JCheckBox("serial");
	static JCheckBox partReflexive = new JCheckBox("generate partially reflexive graphs");
	static JButton enumerate = new JButton("Enumerate");
	
	static Lexer lex = new Lexer();
	static Parser parser = new Parser(lex);

	public Gui() {
		setTitle("Modallogic Enumerator");
		setResizable(false);

		mainPanel.setLayout(new GridLayout(5, 0));
		add(mainPanel);

		mainPanel.add(formulaPanel);
		mainPanel.add(conditionPanel1);
		mainPanel.add(conditionPanel2);
		mainPanel.add(partReflexive);
		mainPanel.add(enumerate);

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

		this.pack();
		setLocationRelativeTo(null);
		setVisible(true);

		enumerate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				final String input = formula.getText();

				if (input.equals(""))
					JOptionPane.showMessageDialog(new JFrame(), "Please insert a formula first.");
				else {
					
					new Thread(new Runnable() {
						public void run() {
							Node root = parser.formula(input);

							ArrayList<Modal> enumModals = generateModals(root);

							if (enumModals.isEmpty())
								JOptionPane.showMessageDialog(new JFrame(), "No satisfying modal generated.");
							else{
								new SatisfyingModals(enumModals, root);
							}
						}
					}).start();
				}
			}
		});
		
		reflexive.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(reflexive.isSelected())
					partReflexive.setSelected(false);;
			}
		});
		
		partReflexive.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(partReflexive.isSelected())
					reflexive.setSelected(false);
			}
		});
	}

	public static void main(String[] args) throws IOException {
		new Gui();

	}

	protected ArrayList<Modal> generateModals(Node root) {
		ArrayList<Modal> enumModals = new ArrayList<Modal>();
		int maxDegree = root.getMaxDegree();
		int diameter = root.getModalDepth() * 2;

		GenerateGraphs genG = new GenerateGraphs(maxDegree, diameter, reflexive.isSelected(), transitive.isSelected(),
				serial.isSelected(), partReflexive.isSelected());
		LabelGraph labelG = new LabelGraph();
		Graph currentGraph = genG.nextGraph();

		while (currentGraph != null) {
			Modal modal = new Modal(currentGraph);

			ArrayList<Modal> labeled = labelG.labelGraph(modal, root);

			if (minimal.isSelected())
				removeNonMinimal(labeled, root);
			
			for (Modal m : labeled) {
				enumModals.add(m);

				// TODO remove/nur fuer tests
				if (!m.mlMc(root))
					System.out.println("Error: Failed to ModalCheck a generated Modal.");
			}

			currentGraph = genG.nextGraph();
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
