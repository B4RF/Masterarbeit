package de.ma.main;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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
import de.ma.modal.SatisfyingModals;
import de.ma.parser.Parser;

public class Gui extends JFrame {
	private static final long serialVersionUID = 1L;

	static JPanel mainPanel = new JPanel(new GridBagLayout());
	static JTextField formula = new JTextField();
	static JPanel optionPanel = new JPanel(new GridBagLayout());
	static JCheckBox reflexive = new JCheckBox("reflexive");
	static JCheckBox transitive = new JCheckBox("transitive");
	static JCheckBox serial = new JCheckBox("serial");
	static JCheckBox partReflexive = new JCheckBox("generate partially reflexive graphs");
	static JCheckBox orbits = new JCheckBox("use orbits");
	static JButton enumerate = new JButton("Enumerate");

	static Lexer lex = new Lexer();
	static Parser parser = new Parser(lex);

	static String input;

	public Gui() {
		setTitle("Modallogic Enumerator");
		setResizable(false);
		add(mainPanel);

		// TODO change font
		// enumerate.setFont(enumerate.getFont().deriveFont(30F));
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
		c.weightx = 0.33;
		optionPanel.add(reflexive, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.33;
		optionPanel.add(transitive, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 0.33;
		optionPanel.add(serial, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.5;
		optionPanel.add(orbits, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.gridwidth = 2;
		optionPanel.add(partReflexive, c);

		this.pack();
		setLocationRelativeTo(null);
		setVisible(true);

		enumerate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				input = formula.getText();

				if (input.equals(""))
					JOptionPane.showMessageDialog(new JFrame(), "Please insert a formula first.");
				else {
					final Node root = parser.formula(input);

					new SatisfyingModals(root, reflexive.isSelected(), transitive.isSelected(), serial.isSelected(),
							partReflexive.isSelected(), orbits.isSelected());
				}
			}
		});

		reflexive.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (reflexive.isSelected())
					partReflexive.setSelected(false);
			}
		});

		partReflexive.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (partReflexive.isSelected()) {
					reflexive.setSelected(false);
					orbits.setSelected(true);
				}
			}
		});

		partReflexive.setToolTipText("Needs orbits for computation.");
		orbits.setToolTipText("Removes duplicates but is time expensive.");
	}

	public static void main(String[] args) throws IOException {
		new Gui();

	}
}
