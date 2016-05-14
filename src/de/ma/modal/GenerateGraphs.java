package de.ma.modal;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GenerateGraphs {
	int maxDegree;
	int diameter;
	int maxVertices;
	int graphIndex;
	ArrayList<Graph> genGraphs = new ArrayList<>();

	public GenerateGraphs(int maxD, int diam) {
		maxDegree = maxD+1;	// because of incoming edges
		diameter = diam;
		maxVertices = 0;

		// number of vertices for full graph
		for (int i = 0; i <= diameter / 2; i++) {
			maxVertices += Math.pow(maxDegree - 1, i);
		}

		graphIndex = 0;
		computeGraphs();
	}

	public Graph nextGraph() {
		if (graphIndex < genGraphs.size()) {
			return genGraphs.get(graphIndex++);
		} else {
			return null;
		}
	}

	private void computeGraphs() {
		for (int curVertices = 1; curVertices <= maxVertices; curVertices++) {

			try {
//				System.out.println("gentreeg.exe -D" + maxDegree + " -Z0:" + diameter + " " + curVertices + " trees.txt");
				Runtime.getRuntime()
						.exec("gentreeg.exe -D" + maxDegree + " -Z0:" + diameter + " " + curVertices + " trees.txt");

				Process directg = Runtime.getRuntime().exec("directg.exe -oT trees.txt");
				BufferedReader in = new BufferedReader(new InputStreamReader(directg.getInputStream()));
				String line;
				String lastLine = ""; // remove duplicates at 2 vertices

				while ((line = in.readLine()) != null) {	
					if (!lastLine.equals(line)) {
//						System.out.println(line);
						addGraphs(line);
					}
					lastLine = line;
				}

				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			File f = new File("trees.txt");
			f.delete();
		}
	}

	private void addGraphs(String line) {
		String[] strOutput = line.split(" ");
		ArrayList<Integer> output = new ArrayList<>();

		for (String str : strOutput) {
			output.add(Integer.parseInt(str));
		}

		Graph g = new Graph();
		for (int i = 0; i < output.get(0); i++) {
			g.addVertex(i);
		}

		for (int i = 2; i < output.get(1)*2+2; i += 2) {
			g.addEdge(output.get(i), output.get(i+1));
		}

		for (Integer v : g.getVertices()) {
			Graph graph = g.clone();
			graph.setInitVertex(v);
			
			// remove graphs with maxdegree in init vertex (no incoming edge therefore one too many)
			// remove graphs with depth different than modal depth
			// remove graphs which have unreachable vertices
			if ((graph.getInitVertex().getEdges().size() != maxDegree) && (graph.getDepth() == diameter / 2) && graph.allVertReach()) {
				genGraphs.add(graph);
			}
		}
	}
}
