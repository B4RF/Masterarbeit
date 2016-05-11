package de.ma.modal;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import att.grappa.Attribute;
import att.grappa.Edge;
import att.grappa.Graph;
import att.grappa.GrappaSupport;
import att.grappa.Node;

public class VisualizeGraph {

	public void visualizeGraph(Modal modal, JLabel label) {
		Graph graph = new Graph("Modal");

		for (Integer v : modal.getVertices()) {
			Node n = new Node(graph, String.valueOf(v));
			n.setAttribute("xlabel", modal.printVarsFromVertex(v));
			
			graph.addNode(n);
		}

		for (Integer origin : modal.getVertices()) {
			for (Integer target : modal.getGraph().getVertex(origin).getEdges()) {
				Edge edge = new Edge(graph, graph.findNodeByName(String
						.valueOf(origin)), graph.findNodeByName(String
						.valueOf(target)));
				
				graph.addEdge(edge);
			}
		}

		Integer init = modal.getGraph().getInitVertex().getIndex();
		if (init != null) {
			Node n = graph.findNodeByName(String.valueOf(init));
			Node invis = new Node(graph);
			Edge start = new Edge(graph, invis, n);
			
			graph.addEdge(start);
			invis.setAttribute(Attribute.STYLE_ATTR, "invis");
			// n.setAttribute(Attribute.COLOR_ATTR, Color.RED);
			// n.setAttribute(Attribute.FILLCOLOR_ATTR, Color.RED);
			// n.setAttribute(Attribute.FONTCOLOR_ATTR, Color.RED);
		}

		String[] processArgs = { "./graphviz-2.38/release/bin/dot.exe",
				"-Tpng", "-o", "./graph.png" }; // Output-Path

		Process formatProcess;
		try {
			formatProcess = Runtime.getRuntime().exec(processArgs, null, null);
			GrappaSupport.filterGraph(graph, formatProcess);
			formatProcess.getOutputStream().close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		BufferedImage myPicture;
		try {
			myPicture = ImageIO.read(new File("./graph.png"));
			label.setIcon(new ImageIcon(myPicture));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
