package de.ma.main;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
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
	
	static JPanel mainPanel = new JPanel(new GridBagLayout());
	static JTextField formula = new JTextField();
	static JPanel optionPanel = new JPanel(new GridBagLayout());
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
		add(mainPanel);
		
		//TODO change font
//		enumerate.setFont(enumerate.getFont().deriveFont(30F));
		GridBagConstraints c = new GridBagConstraints();
		
		JLabel text = new JLabel("Insert modallogic formula:");
		text.setHorizontalAlignment(SwingConstants.CENTER);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.ipadx = 10;
		c.ipady = 5;
		c.weightx = 0.5;
		mainPanel.add(text, c);

		formula.setHorizontalAlignment(SwingConstants.CENTER);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.ipadx = 200;
		c.ipady = 5;
		mainPanel.add(formula, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.ipadx = 0;
		c.ipady = 0;
		mainPanel.add(optionPanel, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.ipady = 5;
		mainPanel.add(enumerate, c);


		optionPanel.setBorder(BorderFactory.createTitledBorder("options"));
		c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5;
		optionPanel.add(minimal, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.5;
		optionPanel.add(reflexive, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.5;
		optionPanel.add(transitive, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0.5;
		optionPanel.add(serial, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 1;
		c.gridwidth = 2;
		optionPanel.add(partReflexive, c);
		
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
