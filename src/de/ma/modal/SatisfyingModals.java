package de.ma.modal;

import java.awt.BorderLayout;
import java.util.ArrayList;

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
	
	ArrayList<Modal> enumModals;
	String formula;
	
	JList<String> enumList = new JList<>();
	JLabel imageLabel = new JLabel();

	int modalIndex;
	
	public SatisfyingModals(ArrayList<Modal> enumM, Node root) {
		this.enumModals = enumM;
		this.formula = computeNNF(root);

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

		this.getContentPane().add(new JLabel(formula, SwingConstants.CENTER), BorderLayout.PAGE_START);
		this.getContentPane().add(new JScrollPane(enumList), BorderLayout.LINE_START);
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
}
